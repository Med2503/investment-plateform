package org.bank.riskservice.util;


import lombok.experimental.UtilityClass;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.model.RiskRejectionReason;

@UtilityClass
public class RiskDecisions {

    private static final String APPROVED_REASON = "trade_approved";

    public static RiskDecision approved() {
        return RiskDecision.builder()
                .status(RiskDecisionStatus.APPROVED)
                .rejectionReason(RiskRejectionReason.NONE)
                .reason(APPROVED_REASON)
                .build();
    }

    public static RiskDecision rejected(RiskRejectionReason rejectionReason, String reason) {
        return RiskDecision.builder()
                .status(RiskDecisionStatus.REJECTED)
                .rejectionReason(rejectionReason)
                .reason(reason)
                .build();
    }

}
