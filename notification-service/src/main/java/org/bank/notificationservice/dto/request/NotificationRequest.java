package org.bank.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bank.notificationservice.entity.NotificationChannel;

public record NotificationRequest(
        @NotBlank
        String userId,
        @NotNull
        NotificationChannel channel,
        @NotBlank
        String subject,
        @NotBlank
        String message
) {
}
