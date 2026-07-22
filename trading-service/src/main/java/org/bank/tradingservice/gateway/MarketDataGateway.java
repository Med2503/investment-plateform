package org.bank.tradingservice.gateway;


import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.client.MarketDataClient;
import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataGateway {

    private final MarketDataClient marketDataClient;

    public MarketAssetResponse getPrice(String symbol) {
        return marketDataClient.getAsset(symbol);
    }
}
