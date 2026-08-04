package org.bank.notificationservice.command;

import org.bank.notificationservice.entity.NotificationChannel;

import java.util.UUID;

public record NotificationCommand(
        String userId,
        NotificationChannel channel,
        String subject,
        String message,
        UUID correlationId
) {
}
