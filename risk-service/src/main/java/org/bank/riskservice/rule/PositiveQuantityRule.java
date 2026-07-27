package org.bank.riskservice.rule;


import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.stereotype.Component;

@Component
public class PositiveQuantityRule implements RiskRule {
    @Override
    public RiskDecision evaluate(RiskContext riskContext) {

        if (riskContext.quantity().signum() <= 0) {
            return RiskDecision.builder()
                    .status(RiskDecisionStatus.REJECTED)
                    .reason("should be greater than 0")
                    .build();
        }
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("Ok")
                .build();
    }
}
