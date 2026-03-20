package com.order.notification.service;

import com.order.notification.entity.Notification;
import com.order.notification.event.OrderCreatedEvent;
import com.order.notification.event.OrderNotificationEvent;
import com.order.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public void handleEvent(OrderNotificationEvent event) {

        Notification notification = new Notification();

        notification.setOrderId(event.getOrderId());
        notification.setType("ORDER_CREATED");
        notification.setMessage(event.getMessage());
        notification.setStatus("SENT");
        notification.setCreatedAt(Instant.now());

        repository.save(notification);
    }
}
