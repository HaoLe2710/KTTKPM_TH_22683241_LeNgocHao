package iuh.fit.se.notificationservice.controller;

import iuh.fit.se.notificationservice.model.NotificationRecord;
import iuh.fit.se.notificationservice.service.NotificationLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationLogService notificationLogService;

    @GetMapping
    public List<NotificationRecord> getAllNotifications() {
        return notificationLogService.getAllNotifications();
    }

    @GetMapping("/booking/{bookingId}")
    public List<NotificationRecord> getNotificationsByBookingId(@PathVariable Long bookingId) {
        return notificationLogService.getNotificationsByBookingId(bookingId);
    }
}
