package org.bank.riskservice.dto.response;

import org.bank.riskservice.model.AMLStatus;

import java.math.BigDecimal;

public record CustomerAMLResponse(
        String userId,

        AMLStatus amlStatus,

        BigDecimal dailyTransactionLimit
) {
}
