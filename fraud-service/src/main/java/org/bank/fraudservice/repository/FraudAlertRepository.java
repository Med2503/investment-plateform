package org.bank.fraudservice.repository;

import org.bank.fraudservice.entity.FraudAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FraudAlertRepository
        extends JpaRepository<FraudAlertEntity, UUID> {

    Optional<FraudAlertEntity> findByTradeId(UUID tradeId);

    boolean existsByTradeId(UUID tradeId);
}
