package org.bank.tradingservice.client;


import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "market-data-service")
public interface MarketDataClient {


    @GetMapping("/api/v1/market-data/{symbol}")
    MarketAssetResponse getAsset(
            @PathVariable String symbol
    );
}
