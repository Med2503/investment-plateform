package org.bank.transferservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/api/v1/accounts/{id}/withdraw")
    void withdraw(@PathVariable UUID id, @RequestParam BigDecimal amount);

    @PostMapping("/api/v1/accounts/{id}/deposit")
    void deposit(@PathVariable UUID id, @RequestParam BigDecimal amount);

}
