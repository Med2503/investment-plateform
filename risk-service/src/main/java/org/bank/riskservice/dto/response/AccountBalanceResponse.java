package org.bank.riskservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponse(
        UUID accountId,
        BigDecimal availableBalance,
        BigDecimal blockedBalance,
        BigDecimal totalBalance
) {
}
