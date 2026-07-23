package org.bank.tradingservice.gateway;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.client.AccountClient;
import org.bank.tradingservice.dto.request.BalanceOperationRequest;
import org.bank.tradingservice.dto.response.AccountResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountGateway {

    private final AccountClient client;

    @Retry(name = "account-service")
    @CircuitBreaker(name = "account-service",
            fallbackMethod = "withdrawFallback")
    public void withdraw(UUID accountId, BigDecimal amount) {
        client.withdraw(accountId, new BalanceOperationRequest(amount));
    }

    public void deposit(UUID accountId, BigDecimal amount) {
        client.deposit(accountId, new BalanceOperationRequest(amount));
    }

    public AccountResponse getAccount(UUID accountId) {
        return client.getAccount(accountId);
    }


    private void withdrawFallback(UUID accountId, BigDecimal amount, Throwable e) {
        throw new RuntimeException("Account service unavailable!" ,e);
    }
}
