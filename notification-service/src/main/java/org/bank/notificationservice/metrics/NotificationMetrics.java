package org.bank.notificationservice.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NotificationMetrics {

    private final Counter emailsSent;
    private final Counter emailsFailed;
    private final Counter kafkaMessagesConsumed;
    private final Counter dlqMessages;

    public NotificationMetrics(MeterRegistry registry) {
        emailsSent = Counter.builder(
                        "notification_email_sent_total"
                )
                .description("Total emails successfully sent")
                .register(registry);
        emailsFailed =
                Counter.builder(
                                "notification_email_failed_total"
                        )
                        .description(
                                "Total email failures"
                        )
                        .register(registry);


        kafkaMessagesConsumed =
                Counter.builder(
                                "notification_kafka_messages_total"
                        )
                        .description(
                                "Total Kafka messages consumed"
                        )
                        .register(registry);

        dlqMessages =
                Counter.builder(
                                "notification_dlq_messages_total"
                        )
                        .description(
                                "Total messages sent to DLQ"
                        )
                        .register(registry);
    }


}
