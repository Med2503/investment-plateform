package org.bank.notificationservice.strategy;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.provider.EmailProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {
    private final EmailProvider emailProvider;

    @Override
    public void send(Notification notification,String content) {
        emailProvider.send(notification, content);
    }

    @Override
    public NotificationChannel supports() {
        return NotificationChannel.EMAIL;
    }


}
