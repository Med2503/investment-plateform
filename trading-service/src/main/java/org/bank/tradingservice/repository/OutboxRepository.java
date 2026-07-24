package org.bank.tradingservice.repository;

import jakarta.persistence.LockModeType;
import org.bank.tradingservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT o
            FROM OutboxEvent o
            WHERE o.published = false
            ORDER BY o.createdAt                                    
            """)
    List<OutboxEvent> findEventsToPublish();
}
