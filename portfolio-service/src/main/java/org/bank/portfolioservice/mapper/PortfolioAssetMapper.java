package org.bank.portfolioservice.mapper;


import org.bank.portfolioservice.dto.response.PortfolioAssetResponse;
import org.bank.portfolioservice.entity.PortfolioAsset;
import org.springframework.stereotype.Component;

@Component
public class PortfolioAssetMapper {

    public PortfolioAssetResponse toResponse(PortfolioAsset asset) {
        return new PortfolioAssetResponse(
                asset.getId(),
                asset.getSymbol(),
                asset.getQuantity(),
                asset.getAveragePrice()
        );
    }
}
