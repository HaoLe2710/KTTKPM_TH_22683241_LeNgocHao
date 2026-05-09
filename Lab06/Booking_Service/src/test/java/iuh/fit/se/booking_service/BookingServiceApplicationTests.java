package iuh.fit.se.booking_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import iuh.fit.se.booking_service.dto.request.CreateBookingRequest;
import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.enums.BookingStatus;
import iuh.fit.se.booking_service.event.EventType;
import iuh.fit.se.booking_service.mapper.BookingMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
class BookingServiceApplicationTests {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void bookingMapperShouldBuildPendingPaymentBooking() {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .userId(1L)
                .movieId(10L)
                .seats(List.of("A1", "A2"))
                .totalAmount(200_000L)
                .build();

        Booking booking = bookingMapper.toEntity(request);

        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertEquals(List.of("A1", "A2"), booking.getSeats());
        assertEquals(200_000L, booking.getTotalAmount());
    }

    @Test
    void bookingMapperShouldCreateBookingCreatedEvent() {
        Booking booking = Booking.builder()
                .id(123L)
                .userId(1L)
                .movieId(10L)
                .seats(List.of("A1", "A2"))
                .totalAmount(200_000L)
                .status(BookingStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.parse("2026-04-21T10:05:00"))
                .build();

        var event = bookingMapper.toCreatedEvent(booking);

        assertEquals(EventType.BOOKING_CREATED, event.getEventType());
        assertEquals(123L, event.getBookingId());
        assertEquals(BigDecimal.valueOf(200_000L), event.getTotalAmount());
        assertNotNull(event.getCreatedAt());
    }
}
