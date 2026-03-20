package com.order.notification.consumer;

import com.order.notification.event.OrderCreatedEvent;
import com.order.notification.event.OrderNotificationEvent;
import com.order.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final NotificationService notificationService;

    public OrderEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "orders.events",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event, Acknowledgment ack) {


        OrderNotificationEvent orderNotificationEvent = OrderNotificationEvent.builder()
                        .orderId(event.getOrderId())
                                .message("Order Created")
                                        .customerId(event.getCustomerId())
                                                .createdAt(event.getCreatedAt())
                                                        .build();
        notificationService.handleEvent(orderNotificationEvent);
        ack.acknowledge();
    }
}