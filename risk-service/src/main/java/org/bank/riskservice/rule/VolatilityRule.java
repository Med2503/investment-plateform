package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.dto.response.MarketAssetResponse;
import org.bank.riskservice.gateway.MarketDataGateway;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskProfile;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.properties.VolatilityProperties;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(RiskRuleOrder.VOLATILITY)
@RequiredArgsConstructor
public class VolatilityRule implements RiskRule {

    private final MarketDataGateway marketDataGateway;
    private final VolatilityProperties volatilityProperties;

    @Override
    public RiskDecision evaluate(RiskContext riskContext) {

        MarketAssetResponse assetResponse = marketDataGateway.getAsset(riskContext.symbol());

        BigDecimal volatility = assetResponse.volatility();

        if (riskContext.riskProfile() == RiskProfile.LOW
                &&
                assetResponse.volatility().compareTo(volatilityProperties.getLowLimit()) > 0) {
            return RiskDecisions.rejected(
                    RiskRejectionReason.HIGH_VOLATILITY,
                    "Asset volatility too high for LOW profile"
            );
        }
        if (riskContext.riskProfile()
                == RiskProfile.MEDIUM
                &&
                volatility.compareTo(
                        volatilityProperties.getMediumLimit()
                ) > 0) {


            return RiskDecisions.rejected(

                    RiskRejectionReason.HIGH_VOLATILITY,

                    "Asset volatility too high for MEDIUM profile"

            );

        }
        return RiskDecisions.approved();
    }
}
