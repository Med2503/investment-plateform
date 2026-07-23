package org.bank.tradingservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.client.MarketDataClient;
import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MarketDataGateway {

    private final MarketDataClient marketDataClient;

    @Retry(name = "market-data-service")
    @CircuitBreaker(name = "market-data-service",
            fallbackMethod = "priceFallback")
    public MarketAssetResponse getPrice(String symbol) {
        return marketDataClient.getAsset(symbol);
    }

    private void priceFallback(
            String symbol,
            Throwable e
    ) {

        throw new RuntimeException(
                "Market data service unavailable",
                e
        );

    }
}
