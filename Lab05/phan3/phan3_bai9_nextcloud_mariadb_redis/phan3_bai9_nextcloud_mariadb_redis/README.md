# Bài tập 9 - Nextcloud với MariaDB + Redis Caching

## Mô tả
Stack này triển khai:
- **Nextcloud** chạy cổng `80`
- **MariaDB** làm database, có volume lưu dữ liệu
- **Redis** làm cache / locking backend

## Yêu cầu đã đáp ứng
- Nextcloud chạy port `80`
- MariaDB có volume cho dữ liệu
- Redis cache
- Các service kết nối qua custom network

## Cấu trúc thư mục
```text
phan3_bai9_nextcloud_mariadb_redis/
├── docker-compose.yml
├── .env.example
├── README.md
└── run.bat
```

## Cách chạy
Giải nén file zip, mở terminal tại thư mục dự án, rồi chạy:

```bash
docker compose up -d
```

Sau đó mở:
- Nextcloud: `http://localhost`

## Thông tin kết nối database khi setup giao diện lần đầu
Nếu Nextcloud yêu cầu nhập database thủ công trong màn hình setup:
- Database user: `nextcloud_user`
- Database password: `nextcloud_pass123`
- Database name: `nextcloud_db`
- Database host: `db`

Redis host:
- `redis`

## Các lệnh hữu ích khi test
Xóa sạch volume để test lại:
```bash
docker compose down -v
```

Xem log:
```bash
docker compose logs app
docker compose logs db
docker compose logs redis
```

Validate file YAML:
```bash
docker compose config
```

## Dừng stack
```bash
docker compose down
```

## Lưu ý
- Lần chạy đầu có thể mất một chút thời gian để Nextcloud khởi tạo.
- Nếu cổng `80` đang bị chiếm, sửa trong `docker-compose.yml`, ví dụ:
  - `"8080:80"`
- Redis ở bài này được dùng làm cache/locking backend cơ bản thông qua biến `REDIS_HOST`.
