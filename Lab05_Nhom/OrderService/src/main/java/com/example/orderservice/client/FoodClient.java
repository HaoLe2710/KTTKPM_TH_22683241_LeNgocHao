package com.example.orderservice.client;

import com.example.orderservice.dto.response.FoodResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FoodClient {

    private final RestTemplate restTemplate;

    @Value("${services.food.base-url}")
    private String foodServiceBaseUrl;

    public FoodClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FoodResponse getFoodById(Long foodId) {
        String base = foodServiceBaseUrl.trim();
        String url = base + "/foods/" + foodId;
        System.out.println(">>> CALL FOOD SERVICE URL = [" + url + "]");
        return restTemplate.getForObject(url, FoodResponse.class);
    }
}