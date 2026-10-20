package org.bank.riskservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.riskservice.entity.RiskDecisionEntity;
import org.bank.sharedevents.event.RiskDecisionStatus;
import org.bank.sharedevents.event.RiskRejectionReason;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskDecisionEventPublisher {

    private final KafkaTemplate<String, RiskDecisionCreatedEvent>
            kafkaTemplate;

    public void publish(RiskDecisionEntity entity) {
        RiskDecisionCreatedEvent event =
                new RiskDecisionCreatedEvent(
                        entity.getId(),
                        entity.getCorrelationId(),
                        entity.getUserId(),
                        entity.getSymbol(),
                        entity.getAmount(),
                        RiskDecisionStatus.valueOf(entity.getStatus().name()),
                        entity.getRejectionReason() == null
                                ? null
                                : RiskRejectionReason.valueOf(
                                entity.getRejectionReason()
                        ),
                        entity.getRejectionMessage(),
                        entity.getCreatedAt()
                );


        kafkaTemplate.send(
                kafkaTopics.RISK_DECISION_CREATED,
                entity.getUserId(),
                event
        );

        log.info(
                "RiskDecisionCreatedEvent published userId={} correlationId={}",
                entity.getUserId(),
                entity.getCorrelationId()
        );
    }
}