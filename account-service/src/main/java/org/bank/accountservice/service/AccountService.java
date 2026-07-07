package org.bank.accountservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.exception.AccountAlreadyExistsByTypeException;
import org.bank.accountservice.exception.AccountCurrencyException;
import org.bank.accountservice.exception.InitialDepositException;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;

    public AccountResponse createAccount(String userId, CreateAccountRequest request) {
        if (request.initialDeposit() != null &&
                request.initialDeposit().compareTo(BigDecimal.ZERO) < 0) {
            throw new InitialDepositException("initial deposit should be positive");
        }
        List<String> currencies = List.of("EUR", "USD", "TND");

        if (!currencies.contains(request.currency())) {
            throw new AccountCurrencyException("Unsupported currency " + request.currency());
        }

        if (accountRepository.existsByUserIdAndType(userId, request.type())) {
            throw new AccountAlreadyExistsByTypeException("user already own this account Type");
        }


        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .userId(userId)
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
