package com.example.paymentandnotificationservice.controller;

import com.example.paymentandnotificationservice.entity.NotificationLog;
import com.example.paymentandnotificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationLog> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
}