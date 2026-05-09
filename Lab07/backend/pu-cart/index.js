const express = require('express');
const Redis = require('ioredis');
const cors = require('cors');
const { createMonitor } = require('../common/monitoring');

const app = express();
app.set('etag', false);
app.use(cors());
app.use(express.json());

const redis = new Redis({ host: '127.0.0.1', port: 6379 });
const monitor = createMonitor({ serviceName: 'pu-cart', port: 8082, redis });
app.use(monitor.middleware);

app.post('/cart/add', async (req, res) => {
  const { userId } = req.body;
  const flowId = req.get('x-flow-id') || req.body.flowId;
  const productId = Number(req.body.productId);
  const key = `cart:${userId}`;
  const requestedQuantity = Number(req.body.quantity || 0);
  const products = JSON.parse(await redis.get('products') || '[]');
  const product = products.find(item => item.id === productId);

  if (!product) {
    monitor.log('warn', 'Cart rejected: product not found', { flowId, stage: '1_CART_ADD', userId, productId });
    return res.status(404).json({ msg: 'Product not found', productId });
  }

  if (requestedQuantity <= 0) {
    monitor.log('warn', 'Cart rejected: invalid quantity', { flowId, stage: '1_CART_ADD', userId, productId, quantity: req.body.quantity });
    return res.status(400).json({ msg: 'Quantity must be greater than 0', productId, stock: product.stock });
  }

  if (product.stock <= 0) {
    monitor.log('warn', 'Cart rejected: out of stock', {
      flowId,
      stage: '1_CART_ADD',
      userId,
      productId,
      stock: product.stock,
    });
    return res.status(409).json({ msg: 'Cannot add to cart because product is out of stock', productId, stock: product.stock });
  }

  const cart = JSON.parse(await redis.get(key) || '[]');

  const exist = cart.find(item => item.productId === productId);
  const nextQuantity = (exist?.quantity || 0) + requestedQuantity;

  if (nextQuantity > product.stock) {
    monitor.log('warn', 'Cart rejected: requested quantity exceeds stock', {
      flowId,
      stage: '1_CART_ADD',
      userId,
      productId,
      requestedQuantity,
      cartQuantity: exist?.quantity || 0,
      stock: product.stock,
    });
    return res.status(409).json({
      msg: 'Cannot add to cart because requested quantity exceeds current stock',
      productId,
      requestedQuantity,
      cartQuantity: exist?.quantity || 0,
      stock: product.stock,
    });
  }

  if (exist) exist.quantity = nextQuantity;
  else cart.push({ productId, quantity: requestedQuantity });

  await redis.set(key, JSON.stringify(cart));

  monitor.log('info', 'Cart accepted: item reserved in cart', {
    flowId,
    stage: '1_CART_ADD',
    userId,
    productId,
    quantity: requestedQuantity,
    lineCount: cart.length,
    totalQuantity: cart.reduce((sum, item) => sum + item.quantity, 0),
    stock: product.stock,
  });

  res.json(cart);
});

app.get('/cart/:userId', async (req, res) => {
  const userId = parseInt(req.params.userId, 10);
  const cart = JSON.parse(await redis.get(`cart:${userId}`) || '[]');

  monitor.log('info', 'Cart viewed', {
    flowId: req.get('x-flow-id'),
    stage: 'CART_VIEW',
    userId,
    lineCount: cart.length,
    totalQuantity: cart.reduce((sum, item) => sum + item.quantity, 0),
  });

  res.json(cart);
});

monitor.routes(app);
app.listen(8082, () => console.log('Cart PU running on 8082'));
