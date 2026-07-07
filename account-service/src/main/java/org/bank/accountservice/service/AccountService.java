package org.bank.accountservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.exception.AccountAlreadyExistsByTypeException;
import org.bank.accountservice.exception.AccountCurrencyException;
import org.bank.accountservice.exception.AccountNotFoundException;
import org.bank.accountservice.exception.InitialDepositException;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;
    private final AccountNumberGenerator generator;
    private final AccountMapper accountMapper;
    private final AccountStatusValidator validator;

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

        String accountNumber;
        do {
            accountNumber = generator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));


        Account account = Account.builder()
                .accountNumber(accountNumber)
                .userId(userId)
                .type(request.type())
                .status(AccountStatus.ACTIVE)
                .currency(request.currency())
                .balance(
                        request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO
                )
                .build();


        Account saved = accountRepository.save(account);

        return accountMapper.toResponse(saved);

    }

    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cannot find account with similar id = " + accountId));


        return accountMapper.toResponse(account);
    }


    public List<AccountResponse> getUserAccounts(String userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }


    public AccountResponse updateStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));


        validator.validate(
                account.getStatus(),
                status
        );


        account.setStatus(status);

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }
}
