package org.bank.notificationservice.provider;

import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushProvider implements NotificationProvider {

    @Override
    public void send(Notification notification) {

        log.info(
                "Push notification sent to user {}",
                notification.getUserId()
        );

    }

}
