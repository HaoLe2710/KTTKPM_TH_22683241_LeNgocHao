# Bài tập 8 - Django + Celery + Redis

Stack này gồm:
- **Django app** chạy cổng `8000`
- **Celery worker** chạy song song
- **Redis** làm message broker / task queue

## Cấu trúc
```text
phan3_bai8_django_celery_redis/
├── docker-compose.yml
├── README.md
├── run.bat
└── app/
    ├── Dockerfile
    ├── requirements.txt
    ├── manage.py
    └── core/
        ├── __init__.py
        ├── asgi.py
        ├── celery.py
        ├── settings.py
        ├── tasks.py
        ├── urls.py
        └── wsgi.py
```

## Cách chạy
```bash
docker compose up -d --build
```

## Truy cập
- Django: `http://localhost:8000`
- Redis: `localhost:6379`

## Test Celery
Mở trình duyệt:
- `http://localhost:8000/` để xem API home
- `http://localhost:8000/run-task/` để gửi một task Celery demo

Sau đó xem log worker:
```bash
docker logs -f celery_worker_bai8
```

## Dừng
```bash
docker compose down
```
