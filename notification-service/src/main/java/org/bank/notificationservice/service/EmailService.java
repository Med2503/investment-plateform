package org.bank.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendTransferCompleted(TransferCompletedEvent event) throws MessagingException {

        Context context = new Context();

        context.setVariable(
                "transferId",
                event.transferId()
        );

        context.setVariable(
                "sourceAccountId",
                event.sourceAccountId()
        );

        context.setVariable(
                "destinationAccountId",
                event.destinationAccountId()
        );

        context.setVariable(
                "amount",
                event.amount()
        );

        context.setVariable(
                "currency",
                event.currency()
        );


        String html =
                templateEngine.process(
                        "transfer-completed",
                        context
                );


        sendEmail(
                "customer@email.com",
                "Transfer Completed",
                html
        );

    }

    public void sendTransferFailed(
            TransferFailedEvent event
    ) throws MessagingException {


        Context context = new Context();


        context.setVariable(
                "transferId",
                event.transferId()
        );

        context.setVariable(
                "sourceAccountId",
                event.sourceAccountId()
        );

        context.setVariable(
                "destinationAccountId",
                event.destinationAccountId()
        );

        context.setVariable(
                "amount",
                event.amount()
        );

        context.setVariable(
                "currency",
                event.currency()
        );

        context.setVariable(
                "failureReason",
                event.failureReason()
        );


        String html =
                templateEngine.process(
                        "transfer-failed",
                        context
                );


        sendEmail(
                "customer@email.com",
                "Transfer Failed",
                html
        );
    }

    private void sendEmail(
            String to,
            String subject,
            String html) throws MessagingException {


        MimeMessage message =
                mailSender.createMimeMessage();


        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );
        helper.setTo(to);

        helper.setSubject(subject);

        helper.setText(
                html,
                true
        );


        mailSender.send(message);
    }
}
