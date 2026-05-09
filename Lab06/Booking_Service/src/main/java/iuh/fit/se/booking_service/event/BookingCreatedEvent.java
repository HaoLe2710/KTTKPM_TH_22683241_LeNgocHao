package iuh.fit.se.booking_service.event;

import iuh.fit.se.booking_service.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {

    private EventType eventType;
    private Long bookingId;
    private Long userId;
    private Long movieId;
    private List<String> seats;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
