package iuh.fit.se.notificationservice.model;

import iuh.fit.se.notificationservice.enums.NotificationStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationRecord(
        Long id,
        Long bookingId,
        Long userId,
        String message,
        NotificationStatus status,
        LocalDateTime createdAt
) {
}
