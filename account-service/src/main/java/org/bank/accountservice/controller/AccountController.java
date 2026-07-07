package org.bank.accountservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request,
                                  @RequestHeader("X-User-Id") String userId) {
        return accountService.createAccount(userId, request);
    }

}
