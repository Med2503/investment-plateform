package org.bank.sharedevents.event.risk;

import org.bank.sharedevents.event.RiskDecisionStatus;
import org.bank.sharedevents.event.RiskRejectionReason;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskDecisionCreatedEvent(

        UUID decisionId,
        UUID correlationId,

        String userId,

        String symbol,

        BigDecimal amount,

        RiskDecisionStatus status,

        RiskRejectionReason rejectionReason,

        String reason,

        Instant createdAt
) {
}
