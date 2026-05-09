package iuh.fit.se.booking_service.service.impl;

import iuh.fit.se.booking_service.dto.request.CreateBookingRequest;
import iuh.fit.se.booking_service.dto.response.BookingResponse;
import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.enums.BookingStatus;
import iuh.fit.se.booking_service.exception.BookingCreationException;
import iuh.fit.se.booking_service.exception.BookingNotFoundException;
import iuh.fit.se.booking_service.exception.SeatAlreadyReservedException;
import iuh.fit.se.booking_service.event.BookingFailedEvent;
import iuh.fit.se.booking_service.event.PaymentCompletedEvent;
import iuh.fit.se.booking_service.mapper.BookingMapper;
import iuh.fit.se.booking_service.repository.BookingRepository;
import iuh.fit.se.booking_service.service.BookingEventPublisher;
import iuh.fit.se.booking_service.service.BookingService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private static final EnumSet<BookingStatus> RESERVED_STATUSES =
            EnumSet.of(BookingStatus.PENDING_PAYMENT, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingEventPublisher bookingEventPublisher;
    private final ConcurrentMap<Long, Object> movieLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        try {
            List<String> normalizedSeats = normalizeSeats(request.getSeats());
            Object movieLock = movieLocks.computeIfAbsent(request.getMovieId(), ignored -> new Object());

            Booking savedBooking;
            synchronized (movieLock) {
                List<String> unavailableSeats = findUnavailableSeats(request.getMovieId(), normalizedSeats);
                if (!unavailableSeats.isEmpty()) {
                    throw new SeatAlreadyReservedException(
                            "Cac ghe da duoc giu hoac dat roi: " + String.join(", ", unavailableSeats));
                }

                Booking booking = bookingMapper.toEntity(request);
                booking.setSeats(normalizedSeats);
                savedBooking = bookingRepository.saveAndFlush(booking);

                // Booking Service only publishes event; payment is handled by another service.
                bookingEventPublisher.publishBookingCreated(savedBooking);
            }

            return bookingMapper.toResponse(savedBooking);
        } catch (SeatAlreadyReservedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to create booking for userId={} and movieId={}",
                    request.getUserId(),
                    request.getMovieId(),
                    ex);
            throw new BookingCreationException("Khong the tao booking", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookings(Long userId) {
        List<Booking> bookings = userId == null
                ? bookingRepository.findAll()
                : bookingRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getReservedSeats(Long movieId) {
        return bookingRepository.findAllByMovieIdAndStatusIn(movieId, RESERVED_STATUSES).stream()
                .flatMap(booking -> booking.getSeats().stream())
                .map(this::normalizeSeat)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        updateBookingStatus(event.getBookingId(), BookingStatus.CONFIRMED, event.getEventType().name());
    }

    @Override
    @Transactional
    public void handleBookingFailed(BookingFailedEvent event) {
        updateBookingStatus(event.getBookingId(), BookingStatus.FAILED, event.getEventType().name());
    }

    private void updateBookingStatus(Long bookingId, BookingStatus newStatus, String sourceEvent) {
        bookingRepository.findById(bookingId).ifPresentOrElse(booking -> {
            if (booking.getStatus() == newStatus) {
                log.info("Ignoring duplicate {} for bookingId={} because status is already {}", sourceEvent, bookingId, newStatus);
                return;
            }

            if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.FAILED) {
                log.warn(
                        "Ignoring {} for bookingId={} because booking is already in terminal status {}",
                        sourceEvent,
                        bookingId,
                        booking.getStatus());
                return;
            }

            booking.setStatus(newStatus);
            bookingRepository.save(booking);
            log.info("Updated bookingId={} to status={} after {}", bookingId, newStatus, sourceEvent);
        }, () -> log.warn("Ignoring {} because bookingId={} does not exist", sourceEvent, bookingId));
    }

    private List<String> findUnavailableSeats(Long movieId, List<String> requestedSeats) {
        Set<String> reservedSeats = new LinkedHashSet<>(getReservedSeats(movieId));
        List<String> unavailableSeats = new ArrayList<>();

        for (String requestedSeat : requestedSeats) {
            if (reservedSeats.contains(requestedSeat)) {
                unavailableSeats.add(requestedSeat);
            }
        }

        return unavailableSeats;
    }

    private List<String> normalizeSeats(List<String> seats) {
        List<String> normalizedSeats = seats.stream()
                .map(this::normalizeSeat)
                .filter(seat -> !seat.isBlank())
                .distinct()
                .toList();

        if (normalizedSeats.isEmpty()) {
            throw new BookingCreationException("Danh sach ghe khong hop le");
        }

        return normalizedSeats;
    }

    private String normalizeSeat(String seat) {
        return seat == null ? "" : seat.trim().toUpperCase(Locale.ROOT);
    }
}
