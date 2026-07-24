package org.bank.notificationservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.Notification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) {

        SimpleMailMessage message = new SimpleMailMessage();

        /*
         TODO
         recupérer l'email user
         */
        message.setTo("user@example.com");

        message.setSubject(notification.getSubject());

        message.setText(notification.getMessage());

        mailSender.send(message);

        log.info(
                "Email sent to user {}",
                notification.getUserId()
        );

    }

}