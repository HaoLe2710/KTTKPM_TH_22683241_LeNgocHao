package iuh.fit.se.paymentservice.service;

import iuh.fit.se.paymentservice.config.PaymentProperties;
import iuh.fit.se.paymentservice.entity.Payment;
import iuh.fit.se.paymentservice.enums.EventType;
import iuh.fit.se.paymentservice.enums.PaymentStatus;
import iuh.fit.se.paymentservice.event.BookingCreatedEvent;
import iuh.fit.se.paymentservice.event.BookingFailedEvent;
import iuh.fit.se.paymentservice.event.PaymentCompletedEvent;
import iuh.fit.se.paymentservice.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessorService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PaymentProperties paymentProperties;

    public void processBookingCreated(BookingCreatedEvent event) {
        log.info("Received BOOKING_CREATED for booking #{}", event.getBookingId());
        log.info("Processing payment for booking #{} with amount {}", event.getBookingId(), event.getTotalAmount());

        // Compare a random number in [0, 99] with the configured success rate to keep the probability easy to tune.
        boolean paymentSucceeded = ThreadLocalRandom.current().nextInt(100) < paymentProperties.getSuccessRate();
        LocalDateTime processedAt = LocalDateTime.now();

        if (paymentSucceeded) {
            log.info("Payment success for booking #{}", event.getBookingId());

            Payment savedPayment = savePayment(event, PaymentStatus.SUCCESS, "Thanh toán thành công");
            PaymentCompletedEvent completedEvent = PaymentCompletedEvent.builder()
                    .eventType(EventType.PAYMENT_COMPLETED)
                    .bookingId(savedPayment.getBookingId())
                    .userId(savedPayment.getUserId())
                    .amount(savedPayment.getAmount())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .message(savedPayment.getMessage())
                    .processedAt(processedAt)
                    .build();

            paymentEventPublisher.publishPaymentCompleted(completedEvent);
            return;
        }

        log.warn("Payment failed for booking #{}", event.getBookingId());

        Payment savedPayment = savePayment(event, PaymentStatus.FAILED, "Thanh toán thất bại");
        BookingFailedEvent failedEvent = BookingFailedEvent.builder()
                .eventType(EventType.BOOKING_FAILED)
                .bookingId(savedPayment.getBookingId())
                .userId(savedPayment.getUserId())
                .amount(savedPayment.getAmount())
                .paymentStatus(PaymentStatus.FAILED)
                .reason(savedPayment.getMessage())
                .processedAt(processedAt)
                .build();

        paymentEventPublisher.publishBookingFailed(failedEvent);
    }

    private Payment savePayment(BookingCreatedEvent event, PaymentStatus paymentStatus, String message) {
        Payment payment = Payment.builder()
                .bookingId(event.getBookingId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .paymentStatus(paymentStatus)
                .message(message)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info(
                "Saved payment history with id={} for booking #{} and status={}",
                savedPayment.getId(),
                savedPayment.getBookingId(),
                savedPayment.getPaymentStatus()
        );
        return savedPayment;
    }
}
