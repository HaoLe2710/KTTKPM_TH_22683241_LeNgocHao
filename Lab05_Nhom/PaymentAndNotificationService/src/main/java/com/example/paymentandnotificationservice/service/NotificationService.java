package com.example.paymentandnotificationservice.service;

import com.example.paymentandnotificationservice.entity.NotificationLog;
import com.example.paymentandnotificationservice.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    public NotificationLog createNotification(Long orderId, Long userId, String message) {
        NotificationLog log = new NotificationLog();
        log.setOrderId(orderId);
        log.setUserId(userId);
        log.setMessage(message);
        log.setCreatedAt(LocalDateTime.now());

        NotificationLog savedLog = notificationLogRepository.save(log);
        System.out.println("[NOTIFICATION] " + message);
        return savedLog;
    }

    public List<NotificationLog> getAllNotifications() {
        return notificationLogRepository.findAll();
    }
}