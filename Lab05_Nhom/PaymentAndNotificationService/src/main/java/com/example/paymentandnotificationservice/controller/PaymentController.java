package com.example.paymentandnotificationservice.controller;


import com.example.paymentandnotificationservice.dto.request.CreatePaymentRequest;
import com.example.paymentandnotificationservice.entity.Payment;
import com.example.paymentandnotificationservice.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}