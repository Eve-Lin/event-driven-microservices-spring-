package com.order.notification.event;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderNotificationEvent {

    private String orderId;
    private String customerId;
    private String message; // computed locally
    private Instant createdAt;
}
