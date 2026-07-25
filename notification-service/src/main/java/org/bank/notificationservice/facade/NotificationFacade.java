package org.bank.notificationservice.facade;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.command.NotificationCommand;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.service.NotificationService;
import org.bank.sharedevents.event.account.DepositCompletedEvent;
import org.bank.sharedevents.event.account.WithdrawCompletedEvent;
import org.bank.sharedevents.event.auth.UserRegisteredEvent;
import org.bank.sharedevents.event.risk.RiskAlertEvent;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;

    public void handleTradeExecuted(TradeExecutedEvent event) {

        NotificationCommand command =
                new NotificationCommand(
                        event.userId(),
                        NotificationChannel.EMAIL,
                        "Trade Executed",
                        null
                );

        notificationService.send(event, command);
    }

    public void handleUserRegistered(UserRegisteredEvent event) {

        NotificationCommand command =
                new NotificationCommand(
                        event.userId().toString(),
                        NotificationChannel.EMAIL,
                        "Welcome",
                        null
                );

        notificationService.send(event, command);
    }

    public void handleDepositCompleted(DepositCompletedEvent event) {

        NotificationCommand command =
                new NotificationCommand(
                        event.userId(),
                        NotificationChannel.EMAIL,
                        "Deposit Completed",
                        null
                );

        notificationService.send(event, command);
    }

    public void handleWithdrawCompleted(WithdrawCompletedEvent event) {

        NotificationCommand command =
                new NotificationCommand(
                        event.userId(),
                        NotificationChannel.EMAIL,
                        "Withdrawal Completed",
                        null
                );

        notificationService.send(event, command);
    }

    public void handleRiskAlert(RiskAlertEvent event) {

        NotificationCommand command =
                new NotificationCommand(
                        event.userId(),
                        NotificationChannel.EMAIL,
                        "Risk Alert",
                        null
                );

        notificationService.send(event, command);
    }

}
