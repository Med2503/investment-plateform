package org.bank.riskservice.gateway;


import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.CustomerRiskProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE",configuration = FeignClientConfiguration.class)
public interface CustomerGateway {

    @GetMapping("/api/v1/customers/{userId}/risk-profile")
    CustomerRiskProfileResponse getRiskProfile(@PathVariable String userId);
}
