package org.bank.portfolioservice.repository;

import org.bank.portfolioservice.entity.PortfolioAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioAssetRepository extends JpaRepository<PortfolioAsset, UUID> {

    List<PortfolioAsset> findByPortfolioId(UUID portfolioId);

    Optional<PortfolioAsset> findByPortfolioIdAndSymbol(
            UUID portfolioId,
            String symbol
    );
}
