package org.bank.riskservice.gateway;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.PortfolioPositionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PORTFOLIO-SERVICE", configuration = FeignClientConfiguration.class)
public interface PortfolioGateway {

    @Retry(name = "portfolio")
    @CircuitBreaker(name = "portfolio")
    @GetMapping("/api/v1/portfolio/{userId}/{symbol}")
    PortfolioPositionResponse getPosition(
            @PathVariable String userId,
            @PathVariable String symbol
    );
}
