package org.bank.notificationservice.factory;


import org.bank.notificationservice.dto.request.NotificationRequest;
import org.bank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {


    public Notification create(NotificationRequest request) {
        return Notification.builder()
                .userId(request.userId())
                .channel(request.channel())
                .subject(request.subject())
                .message(request.message())
                .build();
    }
}
