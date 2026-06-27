package com.world.shop.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    private String eventId;
    private String orderId;
    private String customerId;
    private Double totalAmount;
    private String customerEmail;
    private Instant createdAt;
    private String idempotencyKey;
}