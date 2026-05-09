package iuh.fit.se.booking_service.service;

import iuh.fit.se.booking_service.entity.Booking;

public interface BookingEventPublisher {

    void publishBookingCreated(Booking booking);
}
