
package org.bank.fraudservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudAlertResponse(
        UUID id,
        UUID tradeId,
        String userId,
        String symbol,
        BigDecimal transactionAmount,
        int riskScore,
        FraudStatus status,
        List<FraudReason> reasons,
        Instant createdAt
) {
    public FraudAlertResponse {
        reasons = List.copyOf(reasons);
    }
}
