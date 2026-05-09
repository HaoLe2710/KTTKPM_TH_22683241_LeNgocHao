package iuh.fit.se.booking_service.exception;

public class SeatAlreadyReservedException extends RuntimeException {

    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
