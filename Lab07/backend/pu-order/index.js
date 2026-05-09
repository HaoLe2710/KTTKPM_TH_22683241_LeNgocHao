const express = require('express');
const Redis = require('ioredis');
const cors = require('cors');
const { createMonitor } = require('../common/monitoring');

const app = express();
app.set('etag', false);
app.use(cors());
app.use(express.json());

const redis = new Redis({ host: '127.0.0.1', port: 6379 });
const monitor = createMonitor({ serviceName: 'pu-order', port: 8083, redis });
app.use(monitor.middleware);

const checkoutScript = `
local productsRaw = redis.call('GET', KEYS[1])
local cartRaw = redis.call('GET', KEYS[2])

if not cartRaw or cartRaw == '[]' then
  return cjson.encode({ ok = false, reason = 'empty_cart', status = 400, msg = 'Cart empty' })
end

if not productsRaw then
  return cjson.encode({ ok = false, reason = 'products_missing', status = 500, msg = 'Products data missing' })
end

local products = cjson.decode(productsRaw)
local cart = cjson.decode(cartRaw)
local issues = {}

for _, item in ipairs(cart) do
  local found = false
  for _, product in ipairs(products) do
    if product.id == item.productId then
      found = true
      if product.stock < item.quantity then
        table.insert(issues, {
          productId = item.productId,
          reason = 'insufficient_stock',
          requested = item.quantity,
          stock = product.stock
        })
      end
      break
    end
  end

  if not found then
    table.insert(issues, {
      productId = item.productId,
      reason = 'not_found',
      requested = item.quantity,
      stock = 0
    })
  end
end

if #issues > 0 then
  return cjson.encode({
    ok = false,
    reason = 'insufficient_stock',
    status = 409,
    msg = 'Cannot create order because stock is not enough',
    issues = issues
  })
end

for _, item in ipairs(cart) do
  for _, product in ipairs(products) do
    if product.id == item.productId then
      product.stock = product.stock - item.quantity
      break
    end
  end
end

redis.call('SET', KEYS[1], cjson.encode(products))
redis.call('DEL', KEYS[2])

return cjson.encode({
  ok = true,
  cart = cart,
  products = products
})
`;

async function checkoutWithStockGuard(cartKey, cart) {
  const rawResult = await redis.eval(checkoutScript, 2, 'products', cartKey);
  const result = JSON.parse(rawResult);

  if (!result.ok) {
    return {
      ok: false,
      status: result.status || 409,
      body: {
        msg: result.msg || 'Cannot create order',
        issues: result.issues || [],
      },
      reason: result.reason || 'checkout_rejected',
    };
  }

  return {
    ok: true,
    cart: result.cart,
    products: result.products,
    atomic: true,
  };
}

app.post('/checkout', async (req, res) => {
  const { userId } = req.body;
  const flowId = req.get('x-flow-id') || req.body.flowId;
  const cartKey = `cart:${userId}`;
  const cart = JSON.parse(await redis.get(cartKey) || '[]');

  if (!cart.length) {
    monitor.log('warn', 'Checkout rejected: cart empty', { flowId, stage: '3_CHECKOUT', userId });
    return res.status(400).json({ msg: 'Cart empty' });
  }

  const result = await checkoutWithStockGuard(cartKey, cart);
  if (!result.ok) {
    monitor.log('warn', 'Checkout rejected: stock validation failed', {
      flowId,
      stage: '3_CHECKOUT',
      userId,
      reason: result.reason,
      response: result.body,
    });
    return res.status(result.status).json(result.body);
  }

  monitor.log('info', 'Checkout completed: stock deducted and cart cleared', {
    flowId,
    stage: '3_CHECKOUT',
    userId,
    itemCount: result.cart.length,
    totalQuantity: result.cart.reduce((sum, item) => sum + item.quantity, 0),
    atomic: result.atomic,
    stockAfter: result.cart.map(item => {
      const product = result.products.find(p => p.id === item.productId);
      return { productId: item.productId, stock: product?.stock };
    }),
  });

  res.json({ msg: 'Order created', order: result.cart });
});

monitor.routes(app);
app.listen(8083, () => console.log('Order PU running on 8083'));
