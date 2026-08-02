package org.bank.riskservice.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.bank.sharedevents.kafka.kafkaTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final KafkaTemplate<String, RiskDecisionCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(OutboxEvent event) {

        try {
            RiskDecisionCreatedEvent payload =
                    objectMapper.readValue(event.getPayload(), RiskDecisionCreatedEvent.class);
            kafkaTemplate.send(
                    kafkaTopics.RISK_DECISION_CREATED,
                    payload.userId(),
                    payload
            );

            log.info("Outbox event {} published ", event.getId());

        } catch (Exception e) {
            throw new IllegalStateException("publisher failed", e);
        }

    }
}
