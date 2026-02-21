package com.world.shop.controller;

import com.world.shop.dto.CreateOrderRequest;
import com.world.shop.dto.CreatedOrderResponse;
import com.world.shop.entity.Order;
import com.world.shop.service.OrderService;
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
    public ResponseEntity<CreatedOrderResponse> create(@RequestBody CreateOrderRequest request) {
        Order order = service.createOrder(request.getCustomerId(), request.getTotalAmount());
        return ResponseEntity
                .ok(new CreatedOrderResponse(order.getId(), order.getStatus()));
    }
}