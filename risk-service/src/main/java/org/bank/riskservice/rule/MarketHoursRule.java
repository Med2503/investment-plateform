package org.bank.riskservice.rule;


import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
public class MarketHoursRule implements RiskRule {


    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        LocalTime now = LocalTime.now(ZoneOffset.UTC);

        if (now.isBefore(LocalTime.of(11, 50)) || now.isAfter(LocalTime.of(19, 20))) {
            return RiskDecision.builder()
                    .status(RiskDecisionStatus.REJECTED)
                    .reason("Market is closed")
                    .build();
        }
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("OK")
                .build();
    }
}
