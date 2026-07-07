package org.bank.accountservice.repository;

import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByUserIdAndType(String userId, AccountType type);
}
