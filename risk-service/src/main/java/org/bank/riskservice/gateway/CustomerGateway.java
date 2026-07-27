package org.bank.riskservice.gateway;


import org.bank.riskservice.dto.CustomerRiskProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerGateway {

    @GetMapping("/api/v1/customers/{userId}/risk-profile")
    CustomerRiskProfileResponse getRiskProfile(@PathVariable String userId);
}
