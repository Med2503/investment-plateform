package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.dto.response.DailyExposureResponse;
import org.bank.riskservice.gateway.TradingGateway;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(RiskRuleOrder.DAILY_EXPOSURE)
@RequiredArgsConstructor
public class DailyExposureRule implements RiskRule {
    private final TradingGateway tradingGateway;
    private final RiskProperties riskProperties;


    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        DailyExposureResponse exposureResponse =
                tradingGateway.getDailyExposure(riskContext.userId());


        if (riskContext.totalAmount().compareTo(exposureResponse.remainingExposure()) > 0) {
            return RiskDecisions.rejected(
                    RiskRejectionReason.DAILY_EXPOSURE_EXCEEDED,
                    "Daily exposure exceed"
            );
        }
        return RiskDecisions.approved();
    }
}

