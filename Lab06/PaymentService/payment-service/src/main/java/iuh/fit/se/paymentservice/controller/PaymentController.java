package iuh.fit.se.paymentservice.controller;

import iuh.fit.se.paymentservice.dto.PaymentResponse;
import iuh.fit.se.paymentservice.service.PaymentQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentQueryService paymentQueryService;

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentQueryService.getAllPayments();
    }

    @GetMapping("/booking/{bookingId}")
    public List<PaymentResponse> getPaymentsByBookingId(@PathVariable Long bookingId) {
        return paymentQueryService.getPaymentsByBookingId(bookingId);
    }
}
