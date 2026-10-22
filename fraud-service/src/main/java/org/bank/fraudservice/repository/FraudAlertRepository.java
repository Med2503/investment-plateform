package org.bank.fraudservice.repository;

import org.bank.fraudservice.entity.FraudAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;


public interface FraudAlertRepository
        extends JpaRepository<FraudAlertEntity, UUID> {

    Optional<FraudAlertEntity> findByTradeId(UUID tradeId);

    boolean existsByTradeId(UUID tradeId);

    @Query("""
                SELECT COUNT(f)
                FROM FraudAlertEntity f
                WHERE f.userId = :userId
                  AND f.createdAt >= :since
            """)
    long countRecentAlertsByUserId(
            @Param("userId") String userId,
            @Param("since") Instant since
    );

    Page<FraudAlertEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<FraudAlertEntity> findByUserIdAndFraudStatusOrderByCreatedAtDesc(
            String userId,
            String fraudStatus,
            Pageable pageable
    );

    Page<FraudAlertEntity> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    Page<FraudAlertEntity> findByFraudStatusOrderByCreatedAtDesc(
            String fraudStatus,
            Pageable pageable
    );
}
