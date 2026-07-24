package org.bank.notificationservice.strategy;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.provider.SmsProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsNotificationStrategy implements NotificationStrategy {
    private final SmsProvider smsProvider;


    @Override
    public void send(Notification notification) {
        smsProvider.send(notification);
    }

    @Override
    public NotificationChannel supports() {
        return NotificationChannel.SMS;
    }
}
