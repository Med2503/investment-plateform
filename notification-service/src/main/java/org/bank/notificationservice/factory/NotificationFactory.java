package org.bank.notificationservice.factory;


import org.bank.notificationservice.command.NotificationCommand;
import org.bank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {


    public Notification create(NotificationCommand command) {
        return Notification.builder()
                .userId(command.userId())
                .channel(command.channel())
                .subject(command.subject())
                .message(command.message())
                .build();
    }
}
