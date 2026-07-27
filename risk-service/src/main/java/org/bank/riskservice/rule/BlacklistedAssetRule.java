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
public class BlacklistedAssetRule implements RiskRule {
    private final RiskProperties properties;

    @Override
    public RiskDecision evaluate(RiskContext context) {

        if (properties.getBlackListedSymbols().contains(context.symbol())) {

            return RiskDecisions.rejected(RiskRejectionReason.BLACKLISTED_SYMBOL, "Asset is blacklisted");
        }

        return RiskDecisions.approved();
    }
}
