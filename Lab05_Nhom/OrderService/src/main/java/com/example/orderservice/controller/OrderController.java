package com.example.orderservice.controller;

import com.example.orderservice.dto.request.CreateOrderItemRequest;
import com.example.orderservice.dto.request.CreateOrderRequest;
import com.example.orderservice.dto.request.UpdateOrderStatusRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        System.out.println("===== CREATE ORDER REQUEST =====");
        System.out.println("userId = " + request.getUserId());

        if (request.getItems() != null) {
            for (int i = 0; i < request.getItems().size(); i++) {
                CreateOrderItemRequest item = request.getItems().get(i);
                System.out.println("item[" + i + "].foodId = " + item.getFoodId());
                System.out.println("item[" + i + "].quantity = " + item.getQuantity());
            }
        } else {
            System.out.println("items = null");
        }

        return orderService.createOrder(request);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.updateStatus(id, request);
    }
}