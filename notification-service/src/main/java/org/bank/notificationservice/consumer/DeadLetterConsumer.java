package org.bank.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.metrics.NotificationMetrics;
import org.bank.notificationservice.service.FailedNotificationService;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeadLetterConsumer {
    private final FailedNotificationService service;
    private final NotificationMetrics metrics;

    @KafkaListener(

            topics = {

                    kafkaTopics.TRADE_EXECUTED_DLT,

                    kafkaTopics.USER_REGISTERED_DLT,

                    kafkaTopics.DEPOSIT_COMPLETED_DLT,

                    kafkaTopics.WITHDRAW_COMPLETED_DLT,

                    kafkaTopics.RISK_ALERT_DLT

            },

            groupId = "notification-dlt"

    )
    public void consume(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        metrics.getDlqMessages().increment();
        service.save(topic, payload, extractEventType(topic), "Message in dlt");

    }

    private String extractEventType(String topic) {
        return topic.replace("-dlt", "");
    }

}
