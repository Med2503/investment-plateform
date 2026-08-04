package org.bank.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.command.NotificationCommand;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.service.NotificationService;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RiskDecisionsConsumer {
    private final NotificationService service;

    @KafkaListener(
            topics = kafkaTopics.RISK_DECISION_CREATED,
            groupId = "notification-service"
    )
    public void consume(RiskDecisionCreatedEvent event) {
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
