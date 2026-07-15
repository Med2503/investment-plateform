package org.bank.marketdataservice.repository;

import org.bank.marketdataservice.entity.MarketAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarketAssetRepository extends JpaRepository<MarketAsset, UUID> {
    Optional<MarketAsset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    void deleteBySymbol(String symbol);
}
