package iuh.fit.se.paymentservice.event;

import iuh.fit.se.paymentservice.enums.EventType;
import iuh.fit.se.paymentservice.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    @NotNull
    private EventType eventType;

    @NotNull
    @Positive
    private Long bookingId;

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal amount;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotBlank
    private String message;

    @NotNull
    private LocalDateTime processedAt;
}
