package org.bank.riskservice.rule;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MaxTradeAmountRule implements RiskRule {

    private final RiskProperties properties;


    @Override

    public RiskDecision evaluate(RiskContext riskContext) {
        if (riskContext.totalAmount().compareTo(properties.getMaxTradeAmount()) > 0) {
            return RiskDecision.builder()
                    .status(RiskDecisionStatus.REJECTED)
                    .reason("greater than max trade amount ")
                    .build();
        }
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("ok")
                .build();
    }
}
