const express = require('express');
const cors = require('cors');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 8080;

const USER_SERVICE_URL = process.env.USER_SERVICE_URL || 'http://localhost:8081';
const TOUR_SERVICE_URL = process.env.TOUR_SERVICE_URL || 'http://localhost:8082';
const BOOKING_SERVICE_URL = process.env.BOOKING_SERVICE_URL || 'http://localhost:8083';
const PAYMENT_SERVICE_URL = process.env.PAYMENT_SERVICE_URL || 'http://localhost:8084';

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());

// ─── Health ─────────────────────────────────────────────
app.get('/health', (req, res) => {
  res.json({ service: 'orchestrator-service', status: 'UP' });
});

// ─── Login (proxy to User Service) ─────────────────────
app.post('/login', async (req, res) => {
  try {
    console.log('[Orchestrator] Login request');
    const response = await axios.post(`${USER_SERVICE_URL}/login`, req.body);
    return res.json(response.data);
  } catch (error) {
    const status = error.response?.status || 500;
    const data = error.response?.data || { message: 'Login failed' };
    return res.status(status).json(data);
  }
});

// ─── Register (proxy to User Service) ──────────────────
app.post('/register', async (req, res) => {
  try {
    console.log('[Orchestrator] Register request');
    const response = await axios.post(`${USER_SERVICE_URL}/register`, req.body);
    return res.status(response.status).json(response.data);
  } catch (error) {
    const status = error.response?.status || 500;
    const data = error.response?.data || { message: 'Registration failed' };
    return res.status(status).json(data);
  }
});

// ─── Get Tours (proxy to Tour Service) ─────────────────
app.get('/tours', async (req, res) => {
  try {
    console.log('[Orchestrator] Get tours');
    const response = await axios.get(`${TOUR_SERVICE_URL}/tours`);
    return res.json(response.data);
  } catch (error) {
    const status = error.response?.status || 500;
    const data = error.response?.data || { message: 'Cannot fetch tours' };
    return res.status(status).json(data);
  }
});

// ─── Get Tour Detail (proxy to Tour Service) ───────────
app.get('/tours/:id', async (req, res) => {
  try {
    console.log(`[Orchestrator] Get tour ${req.params.id}`);
    const response = await axios.get(`${TOUR_SERVICE_URL}/tours/${req.params.id}`);
    return res.json(response.data);
  } catch (error) {
    const status = error.response?.status || 500;
    const data = error.response?.data || { message: 'Tour not found' };
    return res.status(status).json(data);
  }
});

// ─── Book Tour (Orchestration Flow) ────────────────────
app.post('/book-tour', async (req, res) => {
  const { userId, tourId } = req.body;

  if (!userId || !tourId) {
    return res.status(400).json({ message: 'userId and tourId are required' });
  }

  try {
    // Step 1: Validate user → User Service
    console.log(`[Orchestrator] Step 1: Validate user ${userId}`);
    const userResponse = await axios.get(`${USER_SERVICE_URL}/users/${userId}`);
    const user = userResponse.data;

    // Step 2: Get tour info → Tour Service
    console.log(`[Orchestrator] Step 2: Get tour ${tourId}`);
    const tourResponse = await axios.get(`${TOUR_SERVICE_URL}/tours/${tourId}`);
    const tour = tourResponse.data;

    // Step 3: Create booking → Booking Service
    console.log('[Orchestrator] Step 3: Create booking');
    const bookingResponse = await axios.post(`${BOOKING_SERVICE_URL}/bookings`, {
      userId: user.id,
      tourId: tour.id,
      tourName: tour.name,
      price: tour.price
    });
    const booking = bookingResponse.data;

    // Step 4: Payment → Payment Service
    console.log('[Orchestrator] Step 4: Make payment');
    const paymentResponse = await axios.post(`${PAYMENT_SERVICE_URL}/payments`, {
      bookingId: booking.id,
      amount: booking.price
    });
    const payment = paymentResponse.data;

    // Step 5: Return confirmation
    console.log(`[Orchestrator] Completed booking ${booking.id} with payment ${payment.status}`);
    return res.status(201).json({
      message: payment.status === 'SUCCESS'
        ? 'Đặt tour thành công! Thanh toán đã được xác nhận.'
        : 'Đặt tour thành công nhưng thanh toán thất bại. Vui lòng thử lại.',
      user,
      tour,
      booking,
      payment
    });
  } catch (error) {
    const status = error.response?.status || 500;
    const message = error.response?.data?.message || error.message || 'Booking orchestration failed';

    console.error('[Orchestrator] Failed:', message);
    return res.status(status).json({
      message: 'Booking orchestration failed',
      detail: message
    });
  }
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Orchestrator Service is running at http://0.0.0.0:${PORT}`);
  console.log('Service URLs:', {
    USER_SERVICE_URL,
    TOUR_SERVICE_URL,
    BOOKING_SERVICE_URL,
    PAYMENT_SERVICE_URL
  });
});
