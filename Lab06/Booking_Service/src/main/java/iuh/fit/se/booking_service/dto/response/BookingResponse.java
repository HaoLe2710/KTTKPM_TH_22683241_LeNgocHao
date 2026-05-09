package iuh.fit.se.booking_service.dto.response;

import iuh.fit.se.booking_service.enums.BookingStatus;
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
public class BookingResponse {

    private Long id;
    private Long userId;
    private Long movieId;
    private List<String> seats;
    private Long totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
