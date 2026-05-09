package iuh.fit.se.booking_service.service;

import iuh.fit.se.booking_service.dto.request.CreateBookingRequest;
import iuh.fit.se.booking_service.dto.response.BookingResponse;
import iuh.fit.se.booking_service.event.BookingFailedEvent;
import iuh.fit.se.booking_service.event.PaymentCompletedEvent;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest request);

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getBookings(Long userId);

    List<String> getReservedSeats(Long movieId);

    void handlePaymentCompleted(PaymentCompletedEvent event);

    void handleBookingFailed(BookingFailedEvent event);
}
