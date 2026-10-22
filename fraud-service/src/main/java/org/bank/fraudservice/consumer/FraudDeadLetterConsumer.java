
package org.bank.fraudservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bank.fraudservice.metrics.FraudMetrics;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudDeadLetterConsumer {

    private static final String EXCEPTION_MESSAGE_HEADER =
            "kafka_dlt-exception-message";

    private final FraudMetrics fraudMetrics;

    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED_DLT,
            groupId = "fraud-service-dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, byte[]> record) {
        fraudMetrics.recordDltEvent();

        String exceptionMessage = readHeader(
                record,
                EXCEPTION_MESSAGE_HEADER
        );

        log.error(
                "Trade event moved to DLT: topic={}, partition={}, offset={}, " +
                        "key={}, payloadBytes={}, exception={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value() == null ? 0 : record.value().length,
                exceptionMessage
        );
    }

    private String readHeader(
            ConsumerRecord<String, byte[]> record,
            String headerName
    ) {
        var header = record.headers().lastHeader(headerName);

        if (header == null || header.value() == null) {
            return "unavailable";
        }

        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
