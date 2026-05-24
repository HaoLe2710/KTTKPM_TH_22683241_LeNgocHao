const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8084;

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());

const payments = [];
let nextPaymentId = 1;

app.get('/health', (req, res) => {
  res.json({ service: 'payment-service', status: 'UP' });
});

app.post('/payments', (req, res) => {
  const { bookingId, amount } = req.body;

  if (!bookingId || typeof amount !== 'number') {
    return res.status(400).json({ message: 'bookingId and amount are required' });
  }

  const isSuccess = Math.random() >= 0.2;
  const payment = {
    id: nextPaymentId++,
    bookingId: Number(bookingId),
    amount,
    status: isSuccess ? 'SUCCESS' : 'FAILED',
    paidAt: new Date().toISOString()
  };

  payments.push(payment);
  return res.status(201).json(payment);
});

app.get('/payments', (req, res) => {
  res.json(payments);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Payment Service is running at http://0.0.0.0:${PORT}`);
});
