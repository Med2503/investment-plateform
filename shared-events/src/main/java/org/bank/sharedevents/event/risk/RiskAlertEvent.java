package org.bank.sharedevents.event.risk;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskAlertEvent(

        UUID alertId,

        String userId,

        String portfolioId,

        String riskLevel,

        String message,

        BigDecimal exposureAmount,

        Instant createdAt
) {
}
