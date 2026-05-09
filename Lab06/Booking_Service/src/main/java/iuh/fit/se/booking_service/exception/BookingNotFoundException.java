package iuh.fit.se.booking_service.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long bookingId) {
        super("Khong tim thay booking voi id = " + bookingId);
    }
}
