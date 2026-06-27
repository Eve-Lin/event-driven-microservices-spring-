package com.world.shop.controller;

import com.world.shop.dto.CreateOrderRequest;
import com.world.shop.dto.CreatedOrderResponse;
import com.world.shop.entity.Order;
import com.world.shop.service.OrderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @RateLimiter(name = "orderRateLimiter")
    public ResponseEntity<CreatedOrderResponse> create(@RequestHeader("Idempotency-Key") String key, @RequestBody CreateOrderRequest request) {
        System.out.println("We are in controller");
        Order order = service.createOrderAndNotify(request.getCustomerId(), request.getTotalAmount(), request.getCustomerEmail(), key);
        return ResponseEntity
                .ok(new CreatedOrderResponse(order.getId(), order.getStatus(), order.getCreatedAt()));
    }
}