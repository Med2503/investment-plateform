package org.bank.accountservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.exception.*;
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
    private final AccountStatusValidator accountStatusValidator;
    private final AccountValidator accountValidator;


    @Transactional
    public AccountResponse createAccount(String userId, CreateAccountRequest request) {
        if (request.initialDeposit() != null && request.initialDeposit().compareTo(BigDecimal.ZERO) < 0) {
            accountValidator.validateInitialDeposit(request.initialDeposit());
        }


        accountValidator.validateCurrency(request.currency());

        accountValidator.validateAccountByUserIdAndType(userId, request);

        String accountNumber;
        do {
            accountNumber = generator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));


        Account account = Account.builder().accountNumber(accountNumber).userId(userId).type(request.type()).status(AccountStatus.ACTIVE).currency(request.currency()).balance(request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO).build();


        Account saved = accountRepository.save(account);

        return accountMapper.toResponse(saved);

    }

    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Cannot find account with similar id = " + accountId));


        return accountMapper.toResponse(account);
    }


    public List<AccountResponse> getUserAccounts(String userId) {
        return accountRepository.findAllByUserId(userId).stream().map(accountMapper::toResponse).toList();
    }

    @Transactional
    public AccountResponse updateStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));


        accountStatusValidator.validate(account.getStatus(), status);


        account.setStatus(status);

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }

    @Transactional
    public AccountResponse deposit(UUID accountId, BigDecimal amount) {
        accountValidator.validateAmount(amount);


        accountValidator.validateAccountActive(accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account don't exist"));

        account.setBalance(account.getBalance().add(amount));

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse withdraw(UUID accountId, BigDecimal amount) {
        accountValidator.validateBalance(accountId, amount);


        accountStatusValidator.validateActiveStatus(accountId);

        accountValidator.validateAmount(amount);

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account don't exist"));


        account.setBalance(account.getBalance().subtract(amount));
        return accountMapper.toResponse(accountRepository.save(account));
    }

}
