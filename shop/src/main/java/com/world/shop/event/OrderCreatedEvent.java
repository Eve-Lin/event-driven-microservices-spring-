package com.world.shop.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    private String orderId;
    private String customerId;
    private Double totalAmount;
    private Instant createdAt;
}