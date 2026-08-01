package org.bank.riskservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.RiskDecisionEntity;
import org.bank.riskservice.model.DecisionStatus;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.repository.RiskDecisionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskDecisionPersistenceService {

    private final RiskDecisionRepository decisionRepository;

    public void save(RiskContext context, RiskDecision decision) {
        RiskDecisionEntity riskDecision =
                RiskDecisionEntity.builder()
                        .userId(context.userId())
                        .symbol(context.symbol())
                        .amount(context.totalAmount())
                        .status(decision.approved() ? DecisionStatus.APPROVED : DecisionStatus.REJECTED)
                        .rejectionReason(decision.rejectionReason() != null ? decision.rejectionReason().name() : null)
                        .rejectionReason(decision.reason())
                        .build();
        decisionRepository.save(riskDecision);
    }
}
