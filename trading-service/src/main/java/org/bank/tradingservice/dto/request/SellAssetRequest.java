package org.bank.tradingservice.dto.request;

import java.math.BigDecimal;

public record SellAssetRequest(

        String symbol,

        BigDecimal quantity
) {
}
