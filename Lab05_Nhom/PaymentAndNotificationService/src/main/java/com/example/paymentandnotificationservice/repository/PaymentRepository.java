package com.example.paymentandnotificationservice.repository;


import com.example.paymentandnotificationservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderIdAndStatusIgnoreCase(Long orderId, String status);
}