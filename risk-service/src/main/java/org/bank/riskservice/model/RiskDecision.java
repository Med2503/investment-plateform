package org.bank.riskservice.model;


import lombok.Builder;

@Builder
public record RiskDecision(
        RiskDecisionStatus status,
        RiskRejectionReason rejectionReason,
        String reason
) {

    public boolean approved() {
        return status == RiskDecisionStatus.APPROVED;
    }
}
