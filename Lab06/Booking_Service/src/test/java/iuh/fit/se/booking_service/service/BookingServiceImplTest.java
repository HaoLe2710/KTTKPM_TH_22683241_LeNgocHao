package iuh.fit.se.booking_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import iuh.fit.se.booking_service.dto.response.BookingResponse;
import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.enums.BookingStatus;
import iuh.fit.se.booking_service.exception.BookingNotFoundException;
import iuh.fit.se.booking_service.event.BookingFailedEvent;
import iuh.fit.se.booking_service.event.EventType;
import iuh.fit.se.booking_service.event.PaymentCompletedEvent;
import iuh.fit.se.booking_service.mapper.BookingMapper;
import iuh.fit.se.booking_service.repository.BookingRepository;
import iuh.fit.se.booking_service.service.impl.BookingServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void getBookingByIdShouldReturnBookingResponse() {
        Booking booking = Booking.builder()
                .id(12L)
                .userId(3L)
                .movieId(5L)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
        BookingResponse response = BookingResponse.builder()
                .id(12L)
                .userId(3L)
                .movieId(5L)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse actual = bookingService.getBookingById(12L);

        assertEquals(12L, actual.getId());
        assertEquals(3L, actual.getUserId());
        assertEquals(5L, actual.getMovieId());
        assertEquals(BookingStatus.PENDING_PAYMENT, actual.getStatus());
        verify(bookingMapper).toResponse(booking);
    }

    @Test
    void getBookingByIdShouldThrowWhenBookingDoesNotExist() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(99L));
    }

    @Test
    void handlePaymentCompletedShouldConfirmPendingBooking() {
        Booking booking = Booking.builder()
                .id(10L)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .eventType(EventType.PAYMENT_COMPLETED)
                .bookingId(10L)
                .userId(1L)
                .amount(BigDecimal.valueOf(200_000L))
                .paymentStatus("SUCCESS")
                .message("Thanh toan thanh cong")
                .processedAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        bookingService.handlePaymentCompleted(event);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void handleBookingFailedShouldFailPendingBooking() {
        Booking booking = Booking.builder()
                .id(11L)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
        BookingFailedEvent event = BookingFailedEvent.builder()
                .eventType(EventType.BOOKING_FAILED)
                .bookingId(11L)
                .userId(1L)
                .amount(BigDecimal.valueOf(200_000L))
                .paymentStatus("FAILED")
                .reason("Thanh toan that bai")
                .processedAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(11L)).thenReturn(Optional.of(booking));

        bookingService.handleBookingFailed(event);

        assertEquals(BookingStatus.FAILED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }
}
