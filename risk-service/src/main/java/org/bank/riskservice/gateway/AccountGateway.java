package org.bank.riskservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.bank.riskservice.config.FeignClientConfiguration;
import org.bank.riskservice.dto.response.AccountBalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ACCOUNT-SERVICE", configuration = FeignClientConfiguration.class)
public interface AccountGateway {

    @CircuitBreaker(name = "account")
    @Retry(name = "account")
    @GetMapping("/api/v1/accounts/{accountId}/balance")
    AccountBalanceResponse getBalance(@PathVariable UUID accountId);
}
