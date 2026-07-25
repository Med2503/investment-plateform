package org.bank.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.notificationservice.metrics.NotificationMetrics;
import org.bank.sharedevents.event.auth.UserRegisteredEvent;

import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthNotificationConsumer {

    private final NotificationFacade facade;
    private final NotificationMetrics metrics;

    @KafkaListener(
            topics = kafkaTopics.USER_REGISTERED,
            groupId = "notification-service"
    )
    public void consume(UserRegisteredEvent event) {

        metrics.getKafkaMessagesConsumed().increment();

        facade.handleUserRegistered(event);

    }

}