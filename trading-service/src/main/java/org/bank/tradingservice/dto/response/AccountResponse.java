package org.bank.tradingservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status
) {
}
