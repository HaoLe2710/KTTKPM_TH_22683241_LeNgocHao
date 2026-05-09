const express = require('express');
const Redis = require('ioredis');
const cors = require('cors');
const { createMonitor } = require('../common/monitoring');

const app = express();
app.set('etag', false);
app.use(cors());
app.use(express.json());

const redis = new Redis({ host: '127.0.0.1', port: 6379 });
const monitor = createMonitor({ serviceName: 'pu-inventory', port: 8084, redis });
app.use(monitor.middleware);

app.get('/stock/:productId', async (req, res) => {
  const productId = parseInt(req.params.productId, 10);
  const flowId = req.get('x-flow-id');
  const products = JSON.parse(await redis.get('products') || '[]');
  const product = products.find(p => p.id === productId);

  monitor.log(product ? 'info' : 'warn', product ? 'Stock checked' : 'Stock check failed: product not found', {
    flowId,
    stage: '2_STOCK_CHECK',
    productId,
    stock: product?.stock || 0,
    found: Boolean(product),
  });

  res.json({ productId: product?.id, stock: product?.stock || 0 });
});

monitor.routes(app);
app.listen(8084, () => console.log('Inventory PU running on 8084'));
