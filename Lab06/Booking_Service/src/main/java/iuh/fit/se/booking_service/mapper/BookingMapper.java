package iuh.fit.se.booking_service.mapper;

import iuh.fit.se.booking_service.dto.request.CreateBookingRequest;
import iuh.fit.se.booking_service.dto.response.BookingResponse;
import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.enums.BookingStatus;
import iuh.fit.se.booking_service.event.BookingCreatedEvent;
import iuh.fit.se.booking_service.event.EventType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(CreateBookingRequest request) {
        return Booking.builder()
                .userId(request.getUserId())
                .movieId(request.getMovieId())
                .seats(request.getSeats())
                .totalAmount(request.getTotalAmount())
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .movieId(booking.getMovieId())
                .seats(booking.getSeats())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public BookingCreatedEvent toCreatedEvent(Booking booking) {
        return BookingCreatedEvent.builder()
                .eventType(EventType.BOOKING_CREATED)
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .movieId(booking.getMovieId())
                .seats(booking.getSeats())
                .totalAmount(BigDecimal.valueOf(booking.getTotalAmount()))
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
