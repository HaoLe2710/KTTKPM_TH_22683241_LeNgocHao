package com.example.paymentandnotificationservice.service;

import com.example.paymentandnotificationservice.client.OrderClient;
import com.example.paymentandnotificationservice.dto.request.CreatePaymentRequest;
import com.example.paymentandnotificationservice.dto.request.UpdateOrderStatusRequest;
import com.example.paymentandnotificationservice.dto.respone.OrderResponse;
import com.example.paymentandnotificationservice.entity.Payment;
import com.example.paymentandnotificationservice.exception.BadRequestException;
import com.example.paymentandnotificationservice.exception.ResourceNotFoundException;
import com.example.paymentandnotificationservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final NotificationService notificationService;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderClient orderClient,
            NotificationService notificationService
    ) {
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
        this.notificationService = notificationService;
    }

    public Payment createPayment(CreatePaymentRequest request) {
        validateCreatePaymentRequest(request);

        OrderResponse order = orderClient.getOrderById(request.getOrderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + request.getOrderId());
        }

        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Order already paid");
        }

        boolean alreadyPaid = paymentRepository.existsByOrderIdAndStatusIgnoreCase(order.getId(), "SUCCESS");
        if (alreadyPaid) {
            throw new BadRequestException("Payment for this order already exists");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("SUCCESS");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus("PAID");
        updateRequest.setPaymentMethod(request.getPaymentMethod());

        orderClient.updateOrderStatus(order.getId(), updateRequest);

        String message = "User " + order.getUserId() + " đã đặt đơn #" + order.getId() + " thành công";
        notificationService.createNotification(order.getId(), order.getUserId(), message);

        return savedPayment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private void validateCreatePaymentRequest(CreatePaymentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request must not be null");
        }
        if (request.getOrderId() == null) {
            throw new BadRequestException("orderId is required");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            throw new BadRequestException("paymentMethod is required");
        }
        String method = request.getPaymentMethod().trim().toUpperCase();
        if (!method.equals("COD") && !method.equals("BANKING")) {
            throw new BadRequestException("paymentMethod must be COD or BANKING");
        }
        request.setPaymentMethod(method);
    }
}