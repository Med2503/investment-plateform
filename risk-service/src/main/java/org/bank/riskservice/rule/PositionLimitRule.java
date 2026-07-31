package org.bank.riskservice.rule;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.dto.response.PortfolioPositionResponse;
import org.bank.riskservice.gateway.PortfolioGateway;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.bank.sharedevents.event.trade.TradeType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionLimitRule implements RiskRule {

    private final PortfolioGateway portfolioGateway;

    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        if (riskContext.isBuy()) {
            return RiskDecisions.approved();
        }

        PortfolioPositionResponse portfolioPositionResponse =
                portfolioGateway.getPosition(
                        riskContext.userId(),
                        riskContext.symbol()
                );
        if (portfolioPositionResponse.quantity().compareTo(riskContext.quantity()) < 0) {
            return RiskDecisions.rejected(
                    RiskRejectionReason.POSITION_LIMIT_EXCEEDED,
                    "Insuffisant shares for sell"
            );

        }
        return RiskDecisions.approved();

    }
}
