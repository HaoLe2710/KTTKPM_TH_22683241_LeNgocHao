# Travel Booking System - Orchestration-Driven SOA

Node.js Express project for Lab 08. The system uses an orchestrator service to coordinate user validation, tour lookup, booking creation, and payment processing.

## Project Structure

```text
orchestrator-service/
user-service/
tour-service/
booking-service/
payment-service/
frontend/
```

## Ports

| Service | Port |
| --- | --- |
| Orchestrator Service | 8080 |
| User Service | 8081 |
| Tour Service | 8082 |
| Booking Service | 8083 |
| Payment Service | 8084 |
| Frontend | Static HTML |

## Run Services

Open one terminal for each service.

```bash
cd user-service
npm install
node index.js
```

```bash
cd tour-service
npm install
node index.js
```

```bash
cd booking-service
npm install
node index.js
```

```bash
cd payment-service
npm install
node index.js
```

```bash
cd orchestrator-service
npm install
node index.js
```

For development mode, run:

```bash
npm run dev
```

## Frontend

Open `frontend/index.html` in a browser.

If you open the frontend from another device in the same LAN, use a simple static server and visit the host machine IP:

```bash
cd frontend
npx http-server -p 5500 -a 0.0.0.0
```

Then open:

```text
http://<your-computer-ip>:5500
```

The frontend calls:

```text
http://<current-hostname>:8080/book-tour
```

## APIs

### User Service

```http
POST /login
Content-Type: application/json

{
  "username": "alice",
  "password": "1234"
}
```

```http
GET /users/1
```

Sample users:

```json
[
  { "id": 1, "username": "alice", "password": "1234" },
  { "id": 2, "username": "bob", "password": "1234" }
]
```

### Tour Service

```http
GET /tours
GET /tours/1
```

Sample tours:

```json
[
  { "id": 1, "name": "Ha Long Bay", "price": 100 },
  { "id": 2, "name": "Da Nang City", "price": 150 }
]
```

### Booking Service

```http
POST /bookings
Content-Type: application/json

{
  "userId": 1,
  "tourId": 1,
  "tourName": "Ha Long Bay",
  "price": 100
}
```

```http
GET /bookings
```

### Payment Service

```http
POST /payments
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 100
}
```

```http
GET /payments
```

Payment status is random with 80% `SUCCESS` and 20% `FAILED`.

### Orchestrator Service

```http
POST /book-tour
Content-Type: application/json

{
  "userId": 1,
  "tourId": 1
}
```

Response:

```json
{
  "user": {
    "id": 1,
    "username": "alice",
    "fullName": "Alice Nguyen"
  },
  "tour": {
    "id": 1,
    "name": "Ha Long Bay",
    "price": 100,
    "location": "Quang Ninh",
    "duration": "2 days 1 night"
  },
  "booking": {
    "id": 1,
    "userId": 1,
    "tourId": 1,
    "tourName": "Ha Long Bay",
    "price": 100,
    "createdAt": "2026-05-11T00:00:00.000Z"
  },
  "payment": {
    "id": 1,
    "bookingId": 1,
    "amount": 100,
    "status": "SUCCESS",
    "paidAt": "2026-05-11T00:00:00.000Z"
  }
}
```

## LAN Configuration

All services listen on `0.0.0.0` and use permissive CORS for lab usage.

By default, the orchestrator calls local services:

```text
USER_SERVICE_URL=http://localhost:8081
TOUR_SERVICE_URL=http://localhost:8082
BOOKING_SERVICE_URL=http://localhost:8083
PAYMENT_SERVICE_URL=http://localhost:8084
```

You can override them when running the orchestrator:

```bash
USER_SERVICE_URL=http://192.168.1.10:8081 node index.js
```
