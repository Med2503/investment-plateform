package org.bank.notificationservice.provider;

import io.micrometer.core.instrument.Timer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.metrics.NotificationMetrics;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProvider {


    private final JavaMailSender mailSender;
    private final NotificationMetrics metrics;


    public void send(
            Notification notification,
            String htmlContent
    ) {

        Timer.Sample sample = Timer.start();
        try {


            MimeMessage message =
                    mailSender.createMimeMessage();


            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );



            /*
             * Temporaire.
             * Plus tard récupéré depuis user-service
             */
            helper.setTo(
                    "user@example.com"
            );


            helper.setSubject(
                    notification.getSubject()
            );


            helper.setText(
                    htmlContent,
                    true
            );


            mailSender.send(message);

            sample.stop(metrics.getEmailProcessingTimer());

            metrics.getEmailsSent().increment();


            log.info(
                    "Email successfully sent to user {}",
                    notification.getUserId()
            );


        } catch (MessagingException e) {

            metrics.getEmailsFailed().increment();

            log.error(
                    "Email sending failed for user {}",
                    notification.getUserId(),
                    e
            );


            throw new RuntimeException(
                    "Unable to send email",
                    e
            );

        }


    }


}