package org.bank.riskservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.MarketAssetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "MARKET-DATA-SERVICE", configuration = FeignClientConfiguration.class)
public interface MarketDataGateway {

    @Retry(name = "marketData")
    @CircuitBreaker(name = "marketData")
    @GetMapping("/api/v1/market-data/{symbol}")
    MarketAssetResponse getAsset(@PathVariable String symbol);
}
