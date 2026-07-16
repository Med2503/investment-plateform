package org.bank.portfolioservice.client;


import org.bank.portfolioservice.dto.response.MarketAssetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "market-data-service"
)
public interface MarketDataClient {


    @GetMapping(
            "/api/v1/market-data/{symbol}"
    )
    MarketAssetResponse getMarketAsset(@PathVariable String symbol);

}
