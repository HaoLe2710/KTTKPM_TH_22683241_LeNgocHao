package iuh.fit.se.paymentservice.dto;

import iuh.fit.se.paymentservice.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PaymentResponse(
        Long id,
        Long bookingId,
        Long userId,
        BigDecimal amount,
        PaymentStatus paymentStatus,
        String message,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
