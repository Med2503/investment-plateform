package org.bank.sharedevents.kafka;

import org.springframework.context.annotation.Configuration;

@Configuration
public class kafkaTopics {
    public static final String USER_REGISTERED = "user-registered";
    public static final String DEPOSIT_COMPLETED = "deposit-completed";
    public static final String WITHDRAW_COMPLETED = "withdraw-completed";
    public static final String RISK_ALERT = "risk-alert";
    public static final String TRADE_EXECUTED = "trade_executed";


    public static final String TRADE_EXECUTED_DLT = "trade-executed-dlt";
    public static final String USER_REGISTERED_DLT = "user-registered-dlt";
    public static final String DEPOSIT_COMPLETED_DLT = "deposit-completed-dlt";
    public static final String WITHDRAW_COMPLETED_DLT = "withdraw-completed-dlt";
    public static final String RISK_ALERT_DLT = "risk-alert-dlt";

    private kafkaTopics() {

    }
}
