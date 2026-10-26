
package org.bank.reportingservice.repository;

import org.bank.reportingservice.entity.TradeRecordEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TradeRecordRepository
        extends JpaRepository<TradeRecordEntity, UUID> {

    Page<TradeRecordEntity> findByUserIdAndExecutedAtGreaterThanEqualAndExecutedAtLessThanOrderByExecutedAtDesc(
            String userId,
            Instant periodStart,
            Instant periodEnd,
            Pageable pageable
    );

    Page<TradeRecordEntity> findByUserIdAndAccountIdAndExecutedAtGreaterThanEqualAndExecutedAtLessThanOrderByExecutedAtDesc(
            String userId,
            UUID accountId,
            Instant periodStart,
            Instant periodEnd,
            Pageable pageable
    );

    List<TradeRecordEntity> findByUserIdAndExecutedAtGreaterThanEqualAndExecutedAtLessThanOrderByExecutedAtAsc(
            String userId,
            Instant periodStart,
            Instant periodEnd
    );

    @Query("""
        SELECT COUNT(t)
        FROM TradeRecordEntity t
        WHERE t.userId = :userId
          AND t.executedAt >= :periodStart
          AND t.executedAt < :periodEnd
        """)
    long countTradesInPeriod(
            @Param("userId") String userId,
            @Param("periodStart") Instant periodStart,
            @Param("periodEnd") Instant periodEnd
    );

    @Query("""
    SELECT COALESCE(SUM(t.quantity * t.executedPrice), 0)
    FROM TradeRecordEntity t
    WHERE t.userId = :userId
      AND t.executedAt >= :periodStart
      AND t.executedAt < :periodEnd
    """)
    BigDecimal sumExecutedNotionalInPeriod(
            @Param("userId") String userId,
            @Param("periodStart") Instant periodStart,
            @Param("periodEnd") Instant periodEnd
    );
}
