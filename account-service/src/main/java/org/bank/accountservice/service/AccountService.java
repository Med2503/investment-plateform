package org.bank.accountservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .userId(request.userId())
                .type(request.type())
                .currency(request.currency())
                .balance(
                        request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO
                )
                .build();
        Account saved = accountRepository.save(account);

        return AccountMapper.toResponse(saved);

    }

    private String generateAccountNumber() {
        return "ACC - " + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
