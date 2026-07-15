package org.bank.marketdataservice.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateMarketPriceRequest(

        @DecimalMin("0.01")
        BigDecimal currentPrice

) {
}
