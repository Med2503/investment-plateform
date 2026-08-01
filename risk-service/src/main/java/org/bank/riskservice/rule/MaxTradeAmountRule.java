package org.bank.riskservice.rule;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(RiskRuleOrder.MAX_TRADE_AMOUNT)
@RequiredArgsConstructor
public class MaxTradeAmountRule implements RiskRule {

    private final RiskProperties properties;


    @Override

    public RiskDecision evaluate(RiskContext riskContext) {
        if (riskContext.totalAmount().compareTo(properties.getMaxTradeAmount()) > 0) {
            RiskDecisions.rejected(RiskRejectionReason.MAX_TRADE_AMOUNT_EXCEEDED,"greater than max trade amount ");
        }
        return RiskDecisions.approved();
    }
}
