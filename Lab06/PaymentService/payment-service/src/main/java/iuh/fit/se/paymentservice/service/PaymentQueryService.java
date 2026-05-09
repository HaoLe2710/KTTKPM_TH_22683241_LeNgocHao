package iuh.fit.se.paymentservice.service;

import iuh.fit.se.paymentservice.dto.PaymentResponse;
import iuh.fit.se.paymentservice.entity.Payment;
import iuh.fit.se.paymentservice.repository.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .message(payment.getMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
