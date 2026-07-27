package org.bank.riskservice.dto;

import java.math.BigDecimal;

public record DailyExposureResponse(
        BigDecimal exposure
) {
}
