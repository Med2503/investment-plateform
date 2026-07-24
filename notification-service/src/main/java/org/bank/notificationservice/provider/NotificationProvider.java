package org.bank.notificationservice.provider;

import org.bank.notificationservice.entity.Notification;

public interface NotificationProvider {
    void send(Notification notification);
}
