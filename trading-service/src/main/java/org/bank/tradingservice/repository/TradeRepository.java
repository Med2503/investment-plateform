package org.bank.tradingservice.repository;

import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    List<Trade> findByUserId(String userId);

    boolean existsByOrderId(UUID orderId);


    @Query("""
                SELECT COALESCE(SUM(t.totalAmount),0)
                FROM Trade t
                WHERE t.userId = :userId
                AND t.status = :status
                AND t.executedAt >= :startOfDay                                    
            
            """)
    BigDecimal calculateTodayExposure(String userId, TradeStatus status, Instant startOfDay);
}
