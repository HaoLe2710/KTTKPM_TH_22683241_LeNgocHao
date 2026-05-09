package iuh.fit.se.booking_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "userId la bat buoc")
    private Long userId;

    @NotNull(message = "movieId la bat buoc")
    private Long movieId;

    @NotEmpty(message = "seats khong duoc rong")
    private List<@NotNull(message = "seat khong duoc null") String> seats;

    @NotNull(message = "totalAmount la bat buoc")
    @Min(value = 0, message = "totalAmount phai >= 0")
    private Long totalAmount;
}
