package org.bank.riskservice.dto.response;

import java.math.BigDecimal;

public record DailyExposureResponse(
        BigDecimal exposure
) {
}
