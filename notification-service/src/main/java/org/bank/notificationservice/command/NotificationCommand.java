package org.bank.notificationservice.command;

import org.bank.notificationservice.entity.NotificationChannel;

public record NotificationCommand(
        String userId,
        NotificationChannel channel,
        String subject,
        String message
) {
}
