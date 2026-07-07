package org.bank.accountservice.dto;

import org.bank.accountservice.entity.AccountStatus;

public record AccountStatusRequest(AccountStatus status) {
}
