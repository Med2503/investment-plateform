package org.bank.riskservice.gateway;


import org.bank.riskservice.dto.AccountBalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountGateway {

    @GetMapping("/api/v1/accounts/{accountId}/balance")
    AccountBalanceResponse getBalance(@PathVariable UUID accountId);
}
