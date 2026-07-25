package org.bank.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.sharedevents.event.risk.RiskAlertEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RiskNotificationConsumer {

    private final NotificationFacade facade;

    @KafkaListener(
            topics = kafkaTopics.RISK_ALERT,
            groupId = "notification-service"
    )
    public void consume(RiskAlertEvent event) {

        facade.handleRiskAlert(event);

    }

}
