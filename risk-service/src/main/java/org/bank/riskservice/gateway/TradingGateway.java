package org.bank.riskservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.DailyExposureResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TRADING-SERVICE", configuration = FeignClientConfiguration.class)
public interface TradingGateway {


    @Retry(name = "trading")
    @CircuitBreaker(name = "trading")
    @GetMapping("/api/v1/trades/exposure/{userId}")
    DailyExposureResponse getDailyExposure(@PathVariable String userId);
}
