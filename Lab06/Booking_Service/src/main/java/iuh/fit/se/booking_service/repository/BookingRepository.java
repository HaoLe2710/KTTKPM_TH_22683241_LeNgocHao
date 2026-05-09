package iuh.fit.se.booking_service.repository;

import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.enums.BookingStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findAllByMovieIdAndStatusIn(Long movieId, Collection<BookingStatus> statuses);
}
