package org.bank.tradingservice.client;


import org.bank.tradingservice.dto.request.BalanceOperationRequest;
import org.bank.tradingservice.dto.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/api/v1/accounts/{id}/withdraw")
    AccountResponse withdraw(
            @PathVariable UUID id,
            @RequestBody BalanceOperationRequest request
    );

    @PostMapping("/api/v1/accounts/{id}/deposit")
    AccountResponse deposit(
            @PathVariable UUID id,
            @RequestBody BalanceOperationRequest request
    );

    @GetMapping("/api/v1/accounts/{id}")
    AccountResponse getAccount(
            @PathVariable UUID id
    );

}
