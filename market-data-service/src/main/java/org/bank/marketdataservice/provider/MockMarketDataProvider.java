package org.bank.marketdataservice.provider;


import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class MockMarketDataProvider implements MarketDataProvider {
    private final Map<String, BigDecimal> prices =
            Map.of(
                    "AAPL", new BigDecimal("220.50"),
                    "TSLA", new BigDecimal("310.20"),
                    "MSFT", new BigDecimal("450.10"),
                    "BTC", new BigDecimal("118000")
            );

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        return prices.getOrDefault(
                symbol.toUpperCase(),
                BigDecimal.ZERO
        );
    }
}
