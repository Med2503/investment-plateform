package org.bank.riskservice.rule;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.config.RiskProperties;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BlacklistedAssetRule implements RiskRule {
    private final RiskProperties properties;

    @Override
    public RiskDecision evaluate(RiskContext context) {

        if (properties.getBlackListedSymbols().contains(context.symbol())) {

            return RiskDecision.builder()
                    .status(RiskDecisionStatus.REJECTED)
                    .reason("Asset is blacklisted")
                    .build();
        }

        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason("OK")
                .build();
    }
}
