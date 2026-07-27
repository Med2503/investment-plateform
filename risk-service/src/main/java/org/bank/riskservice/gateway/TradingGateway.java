package org.bank.riskservice.gateway;


import org.bank.riskservice.dto.DailyExposureResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TRADING-SERVICE")
public interface TradingGateway {

    @GetMapping("/api/v1/trades/exposure/{userId}")
    DailyExposureResponse getDailyExposure(@PathVariable String userId);
}
