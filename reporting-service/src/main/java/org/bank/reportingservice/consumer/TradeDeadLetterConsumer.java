package org.bank.reportingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class TradeDeadLetterConsumer {

    @KafkaListener(
            topics = "trade-executed-dlt",
            groupId = "${spring.kafka.consumer.group-id:reporting-service}-dlt",
            containerFactory = "tradeDeadLetterKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, byte[]> record) {
        byte[] payload = record.value();

        String payloadPreview = payload == null
                ? "<null>"
                : new String(payload, StandardCharsets.UTF_8);

        if (payloadPreview.length() > 500) {
            payloadPreview = payloadPreview.substring(0, 500) + "...";
        }

        log.error(
                "Trade event received in DLQ: topic={}, partition={}, offset={}, key={}, payload={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                payloadPreview
        );
    }
}