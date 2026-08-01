package org.bank.tradingservice.dto.response;

import java.math.BigDecimal;

public record DailyExposureResponse(
        BigDecimal todayExposure,

        BigDecimal remainingExposure,

        BigDecimal maximumExposure
) {
}
