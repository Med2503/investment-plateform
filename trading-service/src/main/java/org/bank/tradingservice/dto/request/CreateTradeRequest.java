package org.bank.tradingservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bank.sharedevents.event.TradeType;


import java.math.BigDecimal;
import java.util.UUID;

public record CreateTradeRequest(
        @NotNull
        UUID accountId,
        @NotBlank
        String symbol,
        @NotNull
        TradeType tradeType,
        @DecimalMin("0.0001")
        BigDecimal quantity
) {
}
