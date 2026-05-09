# Booking Service (CORE)

Booking Service trung tam cho Movie Ticket System, chi tap trung vao:

- Tao booking
- Lay danh sach booking
- Publish event `BOOKING_CREATED` qua Kafka
- Khong goi REST sang Payment Service
- Khong xu ly payment truc tiep

## Cau truc thu muc

```text
Booking_Service/
|-- database/
|   `-- create_booking_db.sql
|-- src/
|   |-- main/
|   |   |-- java/iuh/fit/se/booking_service/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |-- dto/request/
|   |   |   |-- dto/response/
|   |   |   |-- entity/converter/
|   |   |   |-- enums/
|   |   |   |-- event/
|   |   |   |-- exception/
|   |   |   |-- mapper/
|   |   |   |-- repository/
|   |   |   |-- service/impl/
|   |   |   `-- BookingServiceApplication.java
|   |   `-- resources/
|   |       `-- application.yml
|   `-- test/
|       `-- java/iuh/fit/se/booking_service/
|-- pom.xml
`-- README.md
```

## Database MariaDB

Chay file [database/create_booking_db.sql](database/create_booking_db.sql) hoac dung lenh:

```sql
CREATE DATABASE IF NOT EXISTS booking_service_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Bang `bookings` se duoc tao/updated tu dong boi Hibernate voi `spring.jpa.hibernate.ddl-auto=update`.

## Cau hinh quan trong

File cau hinh chinh: [src/main/resources/application.yml](src/main/resources/application.yml)

- Database:
  - `spring.datasource.url`
  - `spring.datasource.username`
  - `spring.datasource.password`
- Kafka:
  - `spring.kafka.bootstrap-servers`
  - `app.kafka.booking-topic`

Mac dinh:

```yaml
server:
  port: 8083

spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/booking_service_db
    username: root
    password: 123456
  kafka:
    bootstrap-servers: localhost:9092

app:
  kafka:
    booking-topic: booking-events
```

Co the override bang environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`
- `BOOKING_EVENTS_TOPIC`
- `SERVER_PORT`

## Cach chay project

Yeu cau:

- Java 21
- MariaDB dang chay
- Kafka dang chay

Lenh chay:

```bash
./mvnw spring-boot:run
```

Hoac tren Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Lenh test:

```powershell
.\mvnw.cmd test
```

## API

### POST /bookings

Request:

```json
{
  "userId": 1,
  "movieId": 10,
  "seats": ["A1", "A2"],
  "totalAmount": 200000
}
```

Response:

```json
{
  "success": true,
  "message": "Tao booking thanh cong",
  "data": {
    "id": 123,
    "userId": 1,
    "movieId": 10,
    "seats": ["A1", "A2"],
    "totalAmount": 200000,
    "status": "PENDING_PAYMENT",
    "createdAt": "2026-04-21T10:05:00"
  }
}
```

Kafka event duoc publish vao topic `booking-events`:

```json
{
  "eventType": "BOOKING_CREATED",
  "bookingId": 123,
  "userId": 1,
  "movieId": 10,
  "seats": ["A1", "A2"],
  "totalAmount": 200000,
  "status": "PENDING_PAYMENT",
  "createdAt": "2026-04-21T10:05:00"
}
```

### GET /bookings

Tat ca booking:

```http
GET /bookings
```

Loc theo `userId`:

```http
GET /bookings?userId=1
```

Response:

```json
{
  "success": true,
  "message": "Lay danh sach booking thanh cong",
  "data": [
    {
      "id": 123,
      "userId": 1,
      "movieId": 10,
      "seats": ["A1", "A2"],
      "totalAmount": 200000,
      "status": "PENDING_PAYMENT",
      "createdAt": "2026-04-21T10:05:00"
    }
  ]
}
```

Neu khong co du lieu:

```json
{
  "success": true,
  "message": "Khong co booking nao",
  "data": []
}
```

## Postman / curl

Tao booking:

```bash
curl --location 'http://localhost:8083/bookings' \
--header 'Content-Type: application/json' \
--data '{
  "userId": 1,
  "movieId": 10,
  "seats": ["A1", "A2"],
  "totalAmount": 200000
}'
```

Lay danh sach booking:

```bash
curl --location 'http://localhost:8083/bookings'
```

Loc theo user:

```bash
curl --location 'http://localhost:8083/bookings?userId=1'
```

## Validation va xu ly loi

- `userId`: bat buoc
- `movieId`: bat buoc
- `seats`: khong duoc rong
- `totalAmount`: phai >= 0

Global exception handler tra response JSON de frontend de xu ly.

Vi du loi:

```json
{
  "success": false,
  "message": "Du lieu request khong hop le",
  "errors": {
    "seats": "seats khong duoc rong",
    "totalAmount": "totalAmount phai >= 0"
  },
  "timestamp": "2026-04-21T16:00:00",
  "path": "/bookings"
}
```

## Luu y kien truc

- Booking Service chi publish event sau khi `saveAndFlush()` thanh cong
- Service khong goi Payment Service bang REST
- Service khong random success/fail cho payment
- Neu publish Kafka that bai, service log ro loi va nem exception de request fail
- Huong nang cao cho production: Outbox Pattern de tranh lech trang thai DB va message broker

## Huong mo rong

Sau nay co the them Kafka consumer trong Booking Service de nghe:

- `PAYMENT_COMPLETED`: cap nhat booking sang `CONFIRMED`
- `BOOKING_FAILED`: cap nhat booking sang `FAILED`

Huong code mo rong:

1. Tao event class moi cho payment callback.
2. Tao Kafka consumer rieng trong package `event` hoac `service`.
3. Tim booking theo `bookingId`.
4. Update `status` va `updatedAt`.
5. Neu can bao toan tinh nhat quan, ap dung Outbox/Inbox Pattern.
