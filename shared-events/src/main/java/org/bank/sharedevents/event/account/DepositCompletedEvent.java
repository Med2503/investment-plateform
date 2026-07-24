package org.bank.sharedevents.event.account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositCompletedEvent(

        UUID transactionId,

        String userId,

        UUID accountId,

        BigDecimal amount,

        BigDecimal newBalance,

        Instant completedAt
) {
}
