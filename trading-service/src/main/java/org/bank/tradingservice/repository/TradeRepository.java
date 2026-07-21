package org.bank.tradingservice.repository;

import org.bank.tradingservice.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    List<Trade> findByUserId(String userId);

    boolean existsByOrderId(UUID orderId);
}
