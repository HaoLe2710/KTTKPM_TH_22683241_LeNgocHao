package com.example.paymentandnotificationservice.repository;

import com.example.paymentandnotificationservice.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}