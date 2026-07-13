package org.bank.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.service.EmailService;
import org.bank.notificationservice.service.SmsService;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final SmsService smsService;


    @KafkaListener(
            topics = "transfer-completed"

    )
    public void consumeTransferCompleted(TransferCompletedEvent event) {


        log.info("Transfer completed reveived {}", event.transferId());

        emailService.sendTransferCompleted(event);
        smsService.sendTransferCompleted(event);
    }


    @KafkaListener(
            topics = "transfer-failed"

    )
    public void consumeTransferFailed(TransferFailedEvent event) {

        log.info(
                "Transfer failed received {}",
                event.transferId()
        );
        emailService.sendTransferFailed(event);
        smsService.sendTransferFailed(event);
    }
}
