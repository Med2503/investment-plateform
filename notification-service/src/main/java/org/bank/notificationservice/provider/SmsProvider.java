package org.bank.notificationservice.provider;

import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsProvider implements NotificationProvider {

    @Override
    public void send(Notification notification) {

        log.info(
                "SMS sent to user {}",
                notification.getUserId()
        );

    }

    /*

    plus tard Twilio ou AWS SNS ou Vonage
     */

}