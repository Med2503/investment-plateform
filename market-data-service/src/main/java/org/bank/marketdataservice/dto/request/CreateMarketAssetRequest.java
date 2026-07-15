package org.bank.marketdataservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateMarketAssetRequest(

        @NotBlank
        String symbol,

        @NotBlank
        String name,

        @DecimalMin("0.01")
        BigDecimal currentPrice,

        @NotBlank
        String currency

) {
}
