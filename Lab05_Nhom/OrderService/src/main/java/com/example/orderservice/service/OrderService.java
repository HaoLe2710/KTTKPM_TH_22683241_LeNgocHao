package com.example.orderservice.service;

import com.example.orderservice.client.FoodClient;
import com.example.orderservice.client.UserClient;
import com.example.orderservice.dto.request.CreateOrderItemRequest;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.UpdateOrderStatusRequest;
import com.example.orderservice.dto.response.FoodResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final FoodClient foodClient;

    public OrderService(
            OrderRepository orderRepository,
            UserClient userClient,
            FoodClient foodClient
    ) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.foodClient = foodClient;
    }

    public Order createOrder(CreateOrderRequest request) {
        validateCreateOrderRequest(request);

        var user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus("PENDING_PAYMENT");
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            FoodResponse food = foodClient.getFoodById(itemRequest.getFoodId());

            if (food == null) {
                throw new RuntimeException("Food not found: " + itemRequest.getFoodId());
            }

            if (Boolean.FALSE.equals(food.getAvailable())) {
                throw new RuntimeException("Food is not available: " + food.getName());
            }

            BigDecimal subtotal = food.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodId(food.getId());
            orderItem.setFoodName(food.getName());
            orderItem.setUnitPrice(food.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = getOrderById(id);
        order.setStatus(request.getStatus());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new RuntimeException("Request must not be null");
        }
        if (request.getUserId() == null) {
            throw new RuntimeException("UserId is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order items must not be empty");
        }
        for (CreateOrderItemRequest item : request.getItems()) {
            if (item.getFoodId() == null) {
                throw new RuntimeException("FoodId is required");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be greater than 0");
            }
        }
    }
}