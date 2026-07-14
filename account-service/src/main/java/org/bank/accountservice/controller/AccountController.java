package org.bank.accountservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.AccountStatusRequest;
import org.bank.accountservice.dto.BalanceOperationRequest;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponse create(@RequestBody @Valid CreateAccountRequest request,
                                  @RequestHeader("X-User-Id") String userId) {
        return accountService.createAccount(userId, request);
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }

    @GetMapping
    public List<AccountResponse> getAccounts(@RequestHeader("X-User-Id") String userId) {
        return accountService.getUserAccounts(userId);
    }


    @PatchMapping("/{id}/status")
    public AccountResponse updateStatus(
            @PathVariable UUID id,
            @RequestBody AccountStatusRequest request
    ) {
        return accountService.updateStatus(
                id,
                request.status()
        );
    }


    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(@PathVariable UUID id, @RequestBody @Valid BalanceOperationRequest request) {
        return accountService.deposit(id, request.amount());
    }

    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(@PathVariable UUID id, @RequestBody @Valid BalanceOperationRequest request) {
        return accountService.withdraw(id, request.amount());
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
    }

}
