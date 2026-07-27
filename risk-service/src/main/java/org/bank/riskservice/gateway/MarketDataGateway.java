package org.bank.riskservice.gateway;


import org.bank.riskservice.dto.MarketAssetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "MARKET-DATA-SERVICE")
public interface MarketDataGateway {

    @GetMapping("/api/v1/market/{symbol}")
    MarketAssetResponse getAsset(@PathVariable String symbol);
}
