package org.bank.riskservice.engine;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.rule.RiskRule;
import org.bank.riskservice.service.RiskDecisionPersistenceService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskEngine {

    private final List<RiskRule> rules;
    private final RiskDecisionPersistenceService service;

    public RiskDecision evaluate(RiskContext riskContext) {


        for (RiskRule rule : rules) {
            RiskDecision decision = rule.evaluate(riskContext);
            if (decision.status() == RiskDecisionStatus.REJECTED) {
                service.save(riskContext, decision);
                return decision;
            }
        }

        RiskDecision decision =
                RiskDecision.builder()
                        .status(RiskDecisionStatus.APPROVED)
                        .reason("Trade approved")
                        .build();
        service.save(riskContext, decision);
        return decision;
    }
}
