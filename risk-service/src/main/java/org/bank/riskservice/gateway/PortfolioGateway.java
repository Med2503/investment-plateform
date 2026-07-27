package org.bank.riskservice.gateway;

import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.PortfolioPositionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PORTFOLIO-SERVICE",configuration = FeignClientConfiguration.class)
public interface PortfolioGateway {


    @GetMapping("/api/v1/portfolio/{userId}/{symbol}")
    PortfolioPositionResponse getPosition(
            @PathVariable String userId,
            @PathVariable String symbol
    );
}
