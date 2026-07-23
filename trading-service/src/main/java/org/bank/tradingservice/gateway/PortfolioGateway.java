package org.bank.tradingservice.gateway;


import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.client.PortfolioClient;
import org.bank.tradingservice.dto.request.SellAssetRequest;
import org.bank.tradingservice.dto.response.PortfolioAssetResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PortfolioGateway {

    private final PortfolioClient portfolioClient;

    public PortfolioAssetResponse getAsset(String userId, String symbol) {
        return portfolioClient.getAsset(
                userId,
                symbol
        );
    }

    public void sellAsset(String userId, String symbol, BigDecimal quantity) {

        portfolioClient.sellAsset(
                userId,
                new SellAssetRequest(
                        symbol,
                        quantity
                )
        );
    }
}
