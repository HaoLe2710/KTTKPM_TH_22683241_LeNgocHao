package iuh.fit.se.paymentservice.repository;

import iuh.fit.se.paymentservice.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderByCreatedAtDesc();

    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);
}
