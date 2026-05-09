package iuh.fit.se.booking_service.controller;

import iuh.fit.se.booking_service.dto.request.CreateBookingRequest;
import iuh.fit.se.booking_service.dto.response.ApiResponse;
import iuh.fit.se.booking_service.dto.response.BookingResponse;
import iuh.fit.se.booking_service.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tao booking thanh cong", response));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable @Positive(message = "bookingId phai lon hon 0") Long bookingId) {
        BookingResponse booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Lay chi tiet booking thanh cong", booking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookings(
            @RequestParam(required = false) @Positive(message = "userId phai lon hon 0") Long userId) {
        List<BookingResponse> bookings = bookingService.getBookings(userId);
        String message = bookings.isEmpty()
                ? "Khong co booking nao"
                : "Lay danh sach booking thanh cong";
        return ResponseEntity.ok(ApiResponse.success(message, bookings));
    }

    @GetMapping("/reserved-seats")
    public ResponseEntity<ApiResponse<List<String>>> getReservedSeats(
            @RequestParam @Positive(message = "movieId phai lon hon 0") Long movieId) {
        List<String> reservedSeats = bookingService.getReservedSeats(movieId);
        String message = reservedSeats.isEmpty()
                ? "Chua co ghe nao duoc giu"
                : "Lay danh sach ghe da giu thanh cong";
        return ResponseEntity.ok(ApiResponse.success(message, reservedSeats));
    }
}
