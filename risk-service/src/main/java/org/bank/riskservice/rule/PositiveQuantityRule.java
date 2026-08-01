package org.bank.riskservice.rule;


import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(RiskRuleOrder.POSITIVE_QUANTITY)
public class PositiveQuantityRule implements RiskRule {
    @Override
    public RiskDecision evaluate(RiskContext riskContext) {

        if (riskContext.quantity().signum() <= 0) {
            RiskDecisions.rejected(RiskRejectionReason.INVALID_QUANTITY, "should be greater than 0");
        }

        return RiskDecisions.approved();
    }
}
