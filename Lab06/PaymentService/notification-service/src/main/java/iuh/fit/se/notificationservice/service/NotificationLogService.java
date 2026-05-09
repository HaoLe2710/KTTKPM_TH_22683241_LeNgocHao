package iuh.fit.se.notificationservice.service;

import iuh.fit.se.notificationservice.enums.NotificationStatus;
import iuh.fit.se.notificationservice.event.BookingFailedEvent;
import iuh.fit.se.notificationservice.event.PaymentCompletedEvent;
import iuh.fit.se.notificationservice.event.UserRegisteredEvent;
import iuh.fit.se.notificationservice.model.NotificationRecord;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationLogService {

    private final AtomicLong sequence = new AtomicLong(0);
    private final CopyOnWriteArrayList<NotificationRecord> notificationStore = new CopyOnWriteArrayList<>();

    public void handleUserRegistered(UserRegisteredEvent event) {
        String welcomeMessage = "Chao mung user #" + event.getUserId() + " (" + event.getUsername() + ") da dang ky";

        log.info(welcomeMessage);

        saveNotification(null, event.getUserId(), welcomeMessage, NotificationStatus.SUCCESS);
    }

    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        String bookingMessage = "Booking #" + event.getBookingId() + " thành công!";
        String userMessage = "User ID " + event.getUserId() + " đã đặt đơn #" + event.getBookingId() + " thành công";

        log.info(bookingMessage);
        log.info(userMessage);

        saveNotification(event.getBookingId(), event.getUserId(), bookingMessage, NotificationStatus.SUCCESS);
        saveNotification(event.getBookingId(), event.getUserId(), userMessage, NotificationStatus.SUCCESS);
    }

    public void handleBookingFailed(BookingFailedEvent event) {
        String bookingMessage = "Booking #" + event.getBookingId() + " thất bại";
        String userMessage = "User ID " + event.getUserId() + " thanh toán booking #" + event.getBookingId() + " thất bại";

        log.warn(bookingMessage);
        log.warn(userMessage);

        saveNotification(event.getBookingId(), event.getUserId(), bookingMessage, NotificationStatus.FAILED);
        saveNotification(event.getBookingId(), event.getUserId(), userMessage, NotificationStatus.FAILED);
    }

    public List<NotificationRecord> getAllNotifications() {
        return notificationStore.stream()
                .sorted(Comparator.comparing(NotificationRecord::createdAt).reversed())
                .toList();
    }

    public List<NotificationRecord> getNotificationsByBookingId(Long bookingId) {
        return notificationStore.stream()
                .filter(record -> record.bookingId() != null && record.bookingId().equals(bookingId))
                .sorted(Comparator.comparing(NotificationRecord::createdAt).reversed())
                .toList();
    }

    private void saveNotification(Long bookingId, Long userId, String message, NotificationStatus status) {
        NotificationRecord record = NotificationRecord.builder()
                .id(sequence.incrementAndGet())
                .bookingId(bookingId)
                .userId(userId)
                .message(message)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        notificationStore.add(record);
    }
}
