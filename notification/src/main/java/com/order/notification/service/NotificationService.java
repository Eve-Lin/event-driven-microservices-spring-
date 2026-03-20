package com.order.notification.service;

import com.order.notification.event.OrderCreatedEvent;
import com.order.notification.event.OrderNotificationEvent;

public interface NotificationService {
    public void handleEvent(OrderNotificationEvent event);
}
