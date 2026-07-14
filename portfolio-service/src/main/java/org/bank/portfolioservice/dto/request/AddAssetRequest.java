package org.bank.portfolioservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddAssetRequest(

        @NotBlank
        String symbol,

        @DecimalMin("0.01")
        BigDecimal quantity,

        @DecimalMin("0.01")
        BigDecimal averagePrice

) {
}
