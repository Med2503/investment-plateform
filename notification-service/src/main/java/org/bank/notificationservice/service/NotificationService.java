package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.bank.notificationservice.command.NotificationCommand;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.factory.NotificationFactory;
import org.bank.notificationservice.factory.NotificationStrategyFactory;
import org.bank.notificationservice.service.template.NotificationTemplateService;
import org.bank.notificationservice.strategy.NotificationStrategy;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j
public class NotificationService {


    private final NotificationFactory notificationFactory;

    private final NotificationStrategyFactory strategyFactory;

    private final NotificationTemplateService templateService;


    public void send(
            Object event,
            NotificationCommand command
    ) {


        Notification notification =
                notificationFactory.create(command);

          // S7i7a mais habech ya9ra log instance
//        log.info(
//                "Notification created correlationId={}",
//                notification.getCorrelationId()
//        );

        String html =
                templateService.build(event);


        NotificationStrategy strategy =
                strategyFactory.getStrategy(
                        notification.getChannel()
                );


        strategy.send(
                notification,
                html
        );


    }

}