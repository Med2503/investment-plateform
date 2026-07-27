package org.bank.riskservice.rule;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MaxTradeAmountRule implements RiskRule {

    @Value("${risk.max-trade-amount}")
    private BigDecimal maxTradeAmount;


    @Override

    public RiskDecision evaluate(RiskContext riskContext) {
        if (riskContext.totalAmount().compareTo(maxTradeAmount) > 0) {
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
