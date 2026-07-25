package org.bank.notificationservice.service.template;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationTemplateService {


    private final TemplateBuilderFactory factory;


    public String build(
            Object event
    ) {


        EmailTemplateBuilder builder =
                factory.getBuilder(
                        event.getClass()
                );


        return builder.build(event);

    }


}