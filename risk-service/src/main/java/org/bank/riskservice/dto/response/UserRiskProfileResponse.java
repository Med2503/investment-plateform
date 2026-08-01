package org.bank.riskservice.dto.response;

import org.bank.riskservice.model.RiskProfile;

public record UserRiskProfileResponse(
        String userId,
        RiskProfile riskProfile
) {
}
