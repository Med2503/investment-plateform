package org.bank.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.notificationservice.metrics.NotificationMetrics;
import org.bank.notificationservice.service.IdempotencyService;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TradeNotificationConsumer {

    private final NotificationFacade facade;
    private final IdempotencyService idempotencyService;
    private final NotificationMetrics metrics;


    @RetryableTopic(

            attempts = "3",

            backoff = @Backoff(

                    delay = 3000,

                    multiplier = 2.0

            ),

            dltTopicSuffix = "-dlt"

    )
    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED,
            groupId = "notification-service"
    )
    public void consume(TradeExecutedEvent event) {

        String eventId = event.tradeId().toString();

        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        metrics.getKafkaMessagesConsumed().increment();


        facade.handleTradeExecuted(event);

        idempotencyService.markAsProcessed(
                eventId,
                TradeExecutedEvent.class.getSimpleName()
        );

    }

}
