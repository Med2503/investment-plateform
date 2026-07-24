package org.bank.notificationservice.dto.response;

import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.entity.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String userId,
        NotificationChannel channel,
        String subject,
        String message,
        NotificationStatus status,
        Instant createdAt,
        Instant sentAt
) {
}
