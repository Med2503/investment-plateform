package org.bank.riskservice.repository;

import org.bank.riskservice.entity.RiskDecisionEntity;
import org.bank.riskservice.model.RiskDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiskDecisionRepository extends JpaRepository<RiskDecisionEntity, UUID> {
}
