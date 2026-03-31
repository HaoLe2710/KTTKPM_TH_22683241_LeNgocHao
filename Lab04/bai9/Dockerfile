FROM redis:latest

# Copy file config vào container
COPY redis.conf /usr/local/etc/redis/redis.conf

# Chạy Redis với config custom
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]