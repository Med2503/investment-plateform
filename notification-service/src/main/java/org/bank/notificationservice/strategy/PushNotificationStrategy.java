package org.bank.notificationservice.strategy;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.provider.PushProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushNotificationStrategy implements NotificationStrategy {

    private final PushProvider pushProvider;

    @Override
    public void send(Notification notification) {
        pushProvider.send(notification);
    }

    @Override
    public NotificationChannel supports() {
        return NotificationChannel.PUSH;
    }
}
