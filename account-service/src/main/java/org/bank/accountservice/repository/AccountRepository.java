package org.bank.accountservice.repository;

import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByUserIdAndType(String userId, AccountType type);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findAllByUserId(String userId);
}
