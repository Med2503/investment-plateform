package org.bank.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.command.NotificationCommand;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.service.IdempotencyService;
import org.bank.notificationservice.service.NotificationService;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RiskDecisionsConsumer {
    private final NotificationService service;
    private final IdempotencyService idempotencyService;

    @RetryableTopic(

            attempts = "3",

            backoff = @Backoff(

                    delay = 3000,

                    multiplier = 2.0

            ),

            dltTopicSuffix = "-dlt"

    )

    @KafkaListener(
            topics = kafkaTopics.RISK_DECISION_CREATED,
            groupId = "notification-service"
    )
    public void consume(RiskDecisionCreatedEvent event) {


        boolean firstProcessing =
                idempotencyService.tryProcess(

                        event.decisionId().toString(),

                        RiskDecisionCreatedEvent.class.getSimpleName()

                );

        if (!firstProcessing) {

            log.info(
                    "Duplicate event {} ignored",
                    event.decisionId()
            );

            return;

        }
        NotificationCommand command = new NotificationCommand(
                event.userId(),
                NotificationChannel.EMAIL,
                "Trade risk decision",
                "Risk decision generated"
        );
        service.send(
                event,
                command
        );

        log.info("Risk decision notification proceed: {}", event.decisionId());
    }
}
