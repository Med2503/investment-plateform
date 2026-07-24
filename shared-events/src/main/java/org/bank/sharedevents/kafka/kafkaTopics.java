package org.bank.sharedevents.kafka;

import org.springframework.context.annotation.Configuration;

@Configuration
public class kafkaTopics {
    public static final String USER_REGISTERED =
            "user-registered";


    public static final String DEPOSIT_COMPLETED =
            "deposit-completed";


    public static final String WITHDRAW_COMPLETED =
            "withdraw-completed";
    public static final String RISK_ALERT =
            "risk-alert";
}
