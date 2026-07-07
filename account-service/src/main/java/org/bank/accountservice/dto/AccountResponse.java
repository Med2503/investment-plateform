package org.bank.accountservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(

        UUID id,
        String accountNumber,
        String userId,
        BigDecimal balance,
        String currency,
        String type,
        String status

) {
}


