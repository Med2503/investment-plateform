package org.bank.riskservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.UserRiskProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE", configuration = FeignClientConfiguration.class)
public interface CustomerGateway {


    @Retry(name = "customer")
    @CircuitBreaker(name = "customer")
    @GetMapping("/api/v1/customers/{userId}/risk-profile")
    UserRiskProfileResponse getRiskProfile(@PathVariable String userId);
}
