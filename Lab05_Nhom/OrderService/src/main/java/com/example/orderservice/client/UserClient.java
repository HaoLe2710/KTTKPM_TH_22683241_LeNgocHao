package com.example.orderservice.client;

import com.example.orderservice.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${services.user.base-url}")
    private String userServiceBaseUrl;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserResponse getUserById(Long userId) {
        String base = userServiceBaseUrl.trim();
        String url = base + "/users/" + userId;
        System.out.println(">>> CALL USER SERVICE URL = [" + url + "]");
        return restTemplate.getForObject(url, UserResponse.class);
    }
}