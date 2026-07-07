package org.bank.accountservice.service;


import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {


    public String generate() {
        long number = ThreadLocalRandom.current()
                .nextLong(1000000000L, 9999999999L);
        return "TN01" + number;
    }
}
