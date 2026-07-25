package org.bank.notificationservice.consumer;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.notificationservice.metrics.NotificationMetrics;
import org.bank.sharedevents.event.account.DepositCompletedEvent;
import org.bank.sharedevents.event.account.WithdrawCompletedEvent;

import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNotificationConsumer {

    private final NotificationFacade facade;
    private final NotificationMetrics metrics;

    @KafkaListener(
            topics = kafkaTopics.DEPOSIT_COMPLETED,
            groupId = "notification-service"
    )
    public void deposit(DepositCompletedEvent event) {


        metrics.getKafkaMessagesConsumed().increment();
        facade.handleDepositCompleted(event);

    }

    @KafkaListener(
            topics = kafkaTopics.WITHDRAW_COMPLETED,
            groupId = "notification-service"
    )
    public void withdraw(WithdrawCompletedEvent event) {
        metrics.getKafkaMessagesConsumed().increment();

        facade.handleWithdrawCompleted(event);

    }

}
