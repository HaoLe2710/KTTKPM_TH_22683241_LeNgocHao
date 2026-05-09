package iuh.fit.se.paymentservice.event;

import iuh.fit.se.paymentservice.enums.EventType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {

    @NotNull
    private EventType eventType;

    @NotNull
    @Positive
    private Long bookingId;

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long movieId;

    @NotEmpty
    private List<String> seats;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalAmount;

    @NotBlank
    private String status;

    @NotNull
    private LocalDateTime createdAt;
}
