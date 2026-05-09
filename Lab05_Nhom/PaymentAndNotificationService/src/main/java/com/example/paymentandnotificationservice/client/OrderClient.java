package com.example.paymentandnotificationservice.client;

import com.example.paymentandnotificationservice.dto.request.UpdateOrderStatusRequest;
import com.example.paymentandnotificationservice.dto.respone.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;

    @Value("${services.order.base-url}")
    private String orderBaseUrl;

    public OrderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderResponse getOrderById(Long orderId) {
        try {
            return restTemplate.getForObject(
                    orderBaseUrl + "/orders/" + orderId,
                    OrderResponse.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        restTemplate.put(orderBaseUrl + "/orders/" + orderId + "/status", request);
    }
}