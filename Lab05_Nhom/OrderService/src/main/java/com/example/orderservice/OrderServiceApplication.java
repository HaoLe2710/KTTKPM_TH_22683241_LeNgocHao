package com.example.orderservice;

import com.example.orderservice.client.FoodClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
