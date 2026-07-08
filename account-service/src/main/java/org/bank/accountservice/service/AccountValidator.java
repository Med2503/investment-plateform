package org.bank.accountservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.exception.*;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRepository accountRepository;


    public void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Amount is mandatory");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

    }

    public void validateAccountActive(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account don't exist"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

    }

    public void validateBalance(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("account not found!")
        );
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insuffisant balance");
        }
    }

    public void validateCurrency(String currency) {
        List<String> currencies = List.of("EUR", "USD", "TND");

        if (!currencies.contains(currency)) {
            throw new AccountCurrencyException("currency not supported" + currency);
        }
    }

    public void validateInitialDeposit(BigDecimal amount) {
        if (amount != null & amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InitialDepositException("Initial deposit should be positive");
        }
    }

    public void validateUniqueAccountType(boolean exists) {
        if (exists) {
            throw new AccountAlreadyExistsByTypeException("account already exists");
        }
    }

    public void validateAccountByUserIdAndType(String userId, CreateAccountRequest request) {
        boolean exists = accountRepository.existsByUserIdAndType(userId, request.type());
        if (exists) {
            throw new AccountExistsByUserIdAndType("account exits by userId and Type");
        }
    }

}
