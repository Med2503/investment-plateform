package org.bank.fraudservice.model;

import java.math.BigDecimal;
import java.util.List;

public record FraudAnalysisResult(
        FraudStatus status,
        BigDecimal transactionAmount,
        int riskScore,
        List<FraudReason> reasons
) {

    public FraudAnalysisResult {
        reasons = List.copyOf(reasons);
    }
}