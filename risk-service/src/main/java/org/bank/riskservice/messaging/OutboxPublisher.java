package org.bank.riskservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final KafkaTemplate<String, RiskDecisionCreatedEvent>
            kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final TextMapGetter<String> TRACEPARENT_GETTER =
            new TextMapGetter<>() {
                @Override
                public Iterable<String> keys(String carrier) {
                    return List.of("traceparent");
                }

                @Override
                public String get(String carrier, String key) {
                    if ("traceparent".equals(key)) {
                        return carrier;
                    }
                    return null;
                }
            };

    public void publish(OutboxEvent event) {
        try {
            RiskDecisionCreatedEvent payload =
                    objectMapper.readValue(
                            event.getPayload(),
                            RiskDecisionCreatedEvent.class
                    );

            Context parentContext = Context.root();

            if (event.getTraceparent() != null
                    && !event.getTraceparent().isBlank()) {
                parentContext =
                        W3CTraceContextPropagator.getInstance()
                                .extract(
                                        Context.root(),
                                        event.getTraceparent(),
                                        TRACEPARENT_GETTER
                                );
            }

            try (Scope ignored = parentContext.makeCurrent()) {
                kafkaTemplate.send(
                        kafkaTopics.RISK_DECISION_CREATED,
                        payload.userId(),
                        payload
                ).get(10, TimeUnit.SECONDS);
            }

            log.info(
                    "Outbox event published eventId={} correlationId={}",
                    event.getId(),
                    payload.correlationId()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while publishing outbox event "
                            + event.getId(),
                    e
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to publish outbox event " + event.getId(),
                    e
            );
        }
    }
}