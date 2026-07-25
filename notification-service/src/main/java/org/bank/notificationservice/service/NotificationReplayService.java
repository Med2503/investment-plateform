package org.bank.notificationservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.FailedNotification;
import org.bank.notificationservice.facade.NotificationFacade;
import org.bank.sharedevents.event.account.DepositCompletedEvent;
import org.bank.sharedevents.event.account.WithdrawCompletedEvent;
import org.bank.sharedevents.event.auth.UserRegisteredEvent;
import org.bank.sharedevents.event.risk.RiskAlertEvent;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationReplayService {

    private final ObjectMapper objectMapper;

    private final NotificationFacade facade;

    public void replay(FailedNotification notification) throws Exception {

        switch (notification.getEventType()) {

            case "trade-executed" -> {

                TradeExecutedEvent event =
                        objectMapper.readValue(
                                notification.getPayload(),
                                TradeExecutedEvent.class
                        );

                facade.handleTradeExecuted(event);

            }

            case "user-registered" -> {

                UserRegisteredEvent event =
                        objectMapper.readValue(
                                notification.getPayload(),
                                UserRegisteredEvent.class
                        );

                facade.handleUserRegistered(event);

            }

            case "deposit-completed" -> {

                DepositCompletedEvent event =
                        objectMapper.readValue(
                                notification.getPayload(),
                                DepositCompletedEvent.class
                        );

                facade.handleDepositCompleted(event);

            }

            case "withdraw-completed" -> {

                WithdrawCompletedEvent event =
                        objectMapper.readValue(
                                notification.getPayload(),
                                WithdrawCompletedEvent.class
                        );

                facade.handleWithdrawCompleted(event);

            }

            case "risk-alert" -> {

                RiskAlertEvent event =
                        objectMapper.readValue(
                                notification.getPayload(),
                                RiskAlertEvent.class
                        );

                facade.handleRiskAlert(event);

            }

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type : "
                                    + notification.getEventType()
                    );

        }

    }

}
