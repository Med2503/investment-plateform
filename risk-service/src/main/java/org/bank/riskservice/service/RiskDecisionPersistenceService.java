package org.bank.riskservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.RiskDecisionEntity;
import org.bank.riskservice.mapper.OutboxEventMapper;
import org.bank.riskservice.model.DecisionStatus;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.repository.RiskDecisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskDecisionPersistenceService {

    private final RiskDecisionRepository decisionRepository;
    private final OutboxService outboxService;
    private final OutboxEventMapper outboxEventMapper;

    @Transactional
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
        RiskDecisionEntity saveEntity = decisionRepository.save(riskDecision);
        outboxService.save(
                outboxEventMapper.toOutboxEvent(saveEntity));

    }
}
