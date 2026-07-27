package org.bank.riskservice.engine;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.rule.RiskRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskEngine {

    private final List<RiskRule> rules;

    public RiskDecision evaluate(RiskContext riskContext) {


        for (RiskRule rule : rules) {
            RiskDecision decision = rule.evaluate(riskContext);
            if (decision.status() == RiskDecisionStatus.REJECTED) {
                return decision;
            }
        }

        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("Trade approved")
                .build();
    }
}
