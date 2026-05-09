const express = require('express');
const Redis = require('ioredis');
const cors = require('cors');
const { createMonitor } = require('../common/monitoring');

const app = express();
app.set('etag', false);
app.use(cors());
app.use(express.json());

const redis = new Redis({ host: '127.0.0.1', port: 6379 });
const monitor = createMonitor({ serviceName: 'pu-product', port: 8081, redis });
app.use(monitor.middleware);

const seedProducts = [
  { id: 1, name: 'Ao thun', price: 150000, stock: 100 },
  { id: 2, name: 'Quan jean', price: 300000, stock: 50 },
  { id: 3, name: 'Giay the thao', price: 500000, stock: 30 },
];

async function resetDemoData() {
  const cartKeys = await redis.keys('cart:*');
  if (cartKeys.length) await redis.del(cartKeys);
  await redis.set('products', JSON.stringify(seedProducts));
  return { productCount: seedProducts.length, clearedCarts: cartKeys.length };
}

resetDemoData().then(result => {
  monitor.log('info', 'Seeded products', result);
});

app.get('/products', async (req, res) => {
  const products = JSON.parse(await redis.get('products') || '[]');
  res.json(products);
});

app.get('/products/:id', async (req, res) => {
  const productId = parseInt(req.params.id, 10);
  const products = JSON.parse(await redis.get('products') || '[]');
  const product = products.find(p => p.id === productId);

  if (req.get('x-silent-log') !== 'true' && req.query.silent !== '1') {
    monitor.log(product ? 'info' : 'warn', product ? 'Product detail viewed' : 'Product detail failed: not found', {
      stage: 'PRODUCT_DETAIL',
      productId,
      found: Boolean(product),
    });
  }

  res.json(product || {});
});

app.post('/products/reset', async (req, res) => {
  const result = await resetDemoData();
  monitor.log('warn', 'Reset demo data', result);
  res.json({ reset: true, ...result });
});

monitor.routes(app);
app.listen(8081, () => console.log('Product PU running on 8081'));
