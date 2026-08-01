package org.bank.riskservice.dto.response;

import org.bank.riskservice.model.DecisionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskDecisionAuditResponse(
        UUID id,

        String userId,

        String symbol,

        BigDecimal amount,

        DecisionStatus status,

        String rejectionReason,

        Instant createdAt
) {
}
