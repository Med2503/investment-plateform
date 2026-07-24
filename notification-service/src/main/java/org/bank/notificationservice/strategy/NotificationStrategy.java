package org.bank.notificationservice.strategy;

import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.entity.NotificationChannel;

public interface NotificationStrategy {

    void send(Notification notification);

    NotificationChannel supports();
}
