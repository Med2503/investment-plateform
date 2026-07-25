package org.bank.notificationservice.provider;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.Notification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProvider {


    private final JavaMailSender mailSender;



    public void send(
            Notification notification,
            String htmlContent
    ) {


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



            log.info(
                    "Email successfully sent to user {}",
                    notification.getUserId()
            );



        }
        catch (MessagingException e) {


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