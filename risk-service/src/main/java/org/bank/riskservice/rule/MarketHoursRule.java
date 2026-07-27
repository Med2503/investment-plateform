package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class MarketHoursRule implements RiskRule {
    private final RiskProperties properties;

    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        LocalTime now = LocalTime.now(ZoneOffset.UTC);

        if (now.isBefore(properties.getMarket().getOpen()) || now.isAfter(properties.getMarket().getClose())) {
            return RiskDecisions.rejected(RiskRejectionReason.MARKET_CLOSED, "Market is closed");
        }
        return RiskDecisions.approved();
    }
}
