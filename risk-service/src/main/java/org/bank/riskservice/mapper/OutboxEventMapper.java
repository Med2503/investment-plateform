package org.bank.riskservice.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.riskservice.entity.RiskDecisionEntity;
import org.bank.riskservice.model.OutboxStatus;
import org.bank.sharedevents.event.RiskDecisionStatus;
import org.bank.sharedevents.event.RiskRejectionReason;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventMapper {

    private final ObjectMapper objectMapper;

    public OutboxEvent toOutboxEvent(RiskDecisionEntity entity) {
        RiskDecisionCreatedEvent event = new RiskDecisionCreatedEvent(
                entity.getId(),
                entity.getCorrelationId(),
                entity.getUserId(),
                entity.getSymbol(),
                entity.getAmount(),
                RiskDecisionStatus.valueOf(
                        entity.getStatus().name()
                ),
                entity.getRejectionReason() == null ? null : RiskRejectionReason.valueOf(entity.getRejectionReason()),
                entity.getRejectionMessage(),
                entity.getCreatedAt()
        );

        try {
            return OutboxEvent.builder()
                    .aggregateType("RiskDecision")
                    .aggregateId(entity.getId())
                    .eventType(RiskDecisionCreatedEvent.class.getSimpleName())
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("error of event serialization", e);
        }
    }
}
