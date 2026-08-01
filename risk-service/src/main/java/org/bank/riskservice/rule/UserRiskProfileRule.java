package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.dto.response.UserRiskProfileResponse;
import org.bank.riskservice.gateway.CustomerGateway;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(RiskRuleOrder.USER_RISK_PROFILE)
@RequiredArgsConstructor
public class UserRiskProfileRule implements RiskRule {

    private final CustomerGateway customerGateway;

    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        UserRiskProfileResponse userRiskProfileResponse =
                customerGateway.getRiskProfile(riskContext.userId());

        return switch (userRiskProfileResponse.riskProfile()) {
            case LOW -> validateLowProfile(riskContext);
            case MEDIUM -> validateMediumProfile(riskContext);
            case HIGH -> RiskDecisions.approved();
        };
    }

    private RiskDecision validateMediumProfile(RiskContext riskContext) {

        BigDecimal maxAmount = new BigDecimal("160000");

        if (riskContext.totalAmount().compareTo(maxAmount) > 0) {
            return RiskDecisions.rejected(
                    RiskRejectionReason.RISK_PROFILE_NOT_ALLOWED,
                    "MEDIUM risk profile limit exceed"
            );
        }
        return RiskDecisions.approved();
    }


    private RiskDecision validateLowProfile(RiskContext riskContext) {

        BigDecimal maxAmount = new BigDecimal("16000");
        if (riskContext.totalAmount().compareTo(maxAmount) > 0) {
            return RiskDecisions.rejected(
                    RiskRejectionReason.RISK_PROFILE_NOT_ALLOWED,
                    "LOW risk profile limit exceed"
            );
        }
        return RiskDecisions.approved();
    }
}
