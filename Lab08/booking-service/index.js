const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8083;

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());

const bookings = [];
let nextBookingId = 1;

app.get('/health', (req, res) => {
  res.json({ service: 'booking-service', status: 'UP' });
});

app.post('/bookings', (req, res) => {
  const { userId, tourId, tourName, price } = req.body;

  if (!userId || !tourId || !tourName || typeof price !== 'number') {
    return res.status(400).json({ message: 'userId, tourId, tourName and price are required' });
  }

  const booking = {
    id: nextBookingId++,
    userId: Number(userId),
    tourId: Number(tourId),
    tourName,
    price,
    createdAt: new Date().toISOString()
  };

  bookings.push(booking);
  return res.status(201).json(booking);
});

app.get('/bookings', (req, res) => {
  res.json(bookings);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Booking Service is running at http://0.0.0.0:${PORT}`);
});
