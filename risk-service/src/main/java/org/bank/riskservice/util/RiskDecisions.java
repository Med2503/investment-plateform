package org.bank.riskservice.util;


import lombok.experimental.UtilityClass;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;

@UtilityClass
public class RiskDecisions {

    private static final String APPROVED_REASON = "trade_approved";

    public static RiskDecision approved() {
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .reason(APPROVED_REASON)
                .build();
    }

    public static RiskDecision rejected(String reason) {
        return RiskDecision.builder()
                .status(RiskDecisionStatus.REJECTED)
                .reason(reason)
                .build();
    }

}
