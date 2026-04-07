# Bài tập 1 - Triển khai WordPress với MySQL bằng Docker Compose

## Cấu trúc
- `docker-compose.yml`
- `.env.example`

## Yêu cầu đã đáp ứng
- WordPress dùng image `wordpress:latest`, chạy cổng `80`
- MySQL dùng image `mysql:5.7`, chạy cổng `3306`
- Volume lưu dữ liệu database tại `/var/lib/mysql`
- Có biến môi trường:
  - `MYSQL_ROOT_PASSWORD`
  - `MYSQL_DATABASE`
  - `MYSQL_USER`
  - `MYSQL_PASSWORD`
- WordPress có `depends_on`
- Hai service dùng custom network `wp_network`

## Cách chạy
Mở terminal tại thư mục chứa file `docker-compose.yml`, rồi chạy:

```bash
docker compose up -d
```

## Kiểm tra
- WordPress: mở trình duyệt vào `http://localhost`
- MySQL: cổng `3306`

## Dừng container
```bash
docker compose down
```

## Dừng và xóa luôn volume dữ liệu
```bash
docker compose down -v
```

## Ghi chú
Nếu máy bạn đang có service khác chiếm cổng `80` hoặc `3306`, hãy sửa phần `ports` trong `docker-compose.yml`.
Ví dụ:
- `"8080:80"` cho WordPress
- `"3307:3306"` cho MySQL
