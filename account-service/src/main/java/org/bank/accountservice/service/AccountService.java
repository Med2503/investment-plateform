package org.bank.accountservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.dto.CreateAccountRequest;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.exception.AccountNotFoundException;
import org.bank.accountservice.kafka.AuditEventProducer;
import org.bank.accountservice.mapper.AccountMapper;
import org.bank.accountservice.repository.AccountRepository;
import org.bank.sharedevents.event.audit.AuditEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;
    private final AccountNumberGenerator generator;
    private final AccountMapper accountMapper;
    private final AccountStatusValidator accountStatusValidator;
    private final AccountValidator accountValidator;
    private final AuditEventProducer auditEventProducer;


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

        auditEventProducer.publish(
                AuditEvent.builder()
                        .eventType("CREATE_ACCOUNT")
                        .accountId(saved.getId().toString())
                        .userId(saved.getUserId())
                        .timestamp(Instant.now())
                        .details("New Account is created")
                        .build()

        );

        return accountMapper.toResponse(saved);

    }

    @Cacheable(
            value = "accounts",
            key = "#accountId"
    )
    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Cannot find account with similar id = " + accountId));


        return accountMapper.toResponse(account);
    }


    public List<AccountResponse> getUserAccounts(String userId) {
        return accountRepository.findAllByUserId(userId).stream().map(accountMapper::toResponse).toList();
    }

    @Transactional
    @CachePut(
            value = "accounts",
            key = "#accountId"
    )
    public AccountResponse updateStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));


        accountStatusValidator.validate(account.getStatus(), status);


        account.setStatus(status);

        Account savedAccount = accountRepository.save(account);

        auditEventProducer.publish(
                AuditEvent.builder()
                        .eventType("ACCOUNT_STATUS_UPDATED")
                        .accountId(savedAccount.getId().toString())
                        .userId(savedAccount.getUserId())
                        .timestamp(Instant.now())
                        .details("Update status to " + savedAccount.getStatus())
                        .build()
        );

        return accountMapper.toResponse(savedAccount);
    }

    @Transactional
    @CachePut(
            value = "accounts",
            key = "#accountId"
    )
    public AccountResponse deposit(UUID accountId, BigDecimal amount) {
        accountValidator.validateAmount(amount);


        accountValidator.validateAccountActive(accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account don't exist"));

        account.setBalance(account.getBalance().add(amount));

        Account saved = accountRepository.save(account);

        auditEventProducer.publish(
                AuditEvent.builder()
                        .eventType("DEPOSIT_AMOUNT")
                        .accountId(saved.getId().toString())
                        .userId(saved.getUserId())
                        .timestamp(Instant.now())
                        .details("deposit " + amount)
                        .build()
        );

        return accountMapper.toResponse(saved);
    }

    @Transactional
    @CachePut(
            value = "accounts",
            key = "#accountId"
    )
    public AccountResponse withdraw(UUID accountId, BigDecimal amount) {
        accountValidator.validateBalance(accountId, amount);


        accountStatusValidator.validateActiveStatus(accountId);

        accountValidator.validateAmount(amount);

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account don't exist"));


        account.setBalance(account.getBalance().subtract(amount));
        Account save = accountRepository.save(account);

        auditEventProducer.publish(
                AuditEvent.builder()
                        .eventType("WITHDRAW_MONEY")
                        .accountId(save.getId().toString())
                        .userId(save.getUserId())
                        .timestamp(Instant.now())
                        .details("Withdraw " + amount)

                        .build()
        );

        return accountMapper.toResponse(save);
    }


    @CacheEvict(
            value = "accounts",
            key = "#accountId"
    )
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow();
        accountRepository.delete(account);

    }
}
