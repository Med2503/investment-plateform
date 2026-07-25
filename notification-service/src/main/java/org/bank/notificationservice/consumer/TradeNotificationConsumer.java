package org.bank.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TradeNotificationConsumer {

    private final NotificationFacade facade;

    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED,
            groupId = "notification-service"
    )
    public void consume(TradeExecutedEvent event) {

        facade.handleTradeExecuted(event);

    }

}
