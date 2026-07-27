package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SupportedSymbolRule implements RiskRule {

    private final RiskProperties properties;


    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        if (!properties.getSupportedSymbols().contains(riskContext.symbol())) {
            return RiskDecisions.rejected(RiskRejectionReason.UNSUPPORTED_SYMBOL, "unsupported symbol");
        }
        return RiskDecisions.approved();
    }
}
