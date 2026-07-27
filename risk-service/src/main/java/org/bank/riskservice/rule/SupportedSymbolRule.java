package org.bank.riskservice.rule;


import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SupportedSymbolRule implements RiskRule {

    private static final Set<String> supported = Set.of(

            "INS",
            "FBC",
            "DAX",
            "FNB",
            "GMARK"
    );


    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        if (!supported.contains(riskContext.symbol())) {
            return RiskDecision.builder()
                    .status(RiskDecisionStatus.REJECTED)
                    .reason("Unsupported symbol")
                    .build();
        }
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("OK")
                .build();
    }
}
