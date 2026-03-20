package com.world.shop.controller;

import com.world.shop.dto.CreateOrderRequest;
import com.world.shop.dto.CreatedOrderResponse;
import com.world.shop.entity.Order;
import com.world.shop.service.OrderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @RateLimiter(name = "orderRateLimiter")
    public ResponseEntity<CreatedOrderResponse> create(@RequestBody CreateOrderRequest request) {
        Order order = service.createOrder(request.getCustomerId(), request.getTotalAmount(), request.getCustomerEmail());
        return ResponseEntity
                .ok(new CreatedOrderResponse(order.getId(), order.getStatus()));
    }
}