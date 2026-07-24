package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.auth.UserRegisteredEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@RequiredArgsConstructor
public class WelcomeEmailTemplateBuilder
        implements EmailTemplateBuilder {


    private final SpringTemplateEngine templateEngine;


    @Override
    public String build(Object event) {


        UserRegisteredEvent userEvent =
                (UserRegisteredEvent) event;


        Context context = new Context();


        context.setVariable(
                "firstName",
                userEvent.firstName()
        );


        return templateEngine.process(
                "emails/welcome",
                context
        );

    }


    @Override
    public Class<?> supports() {

        return UserRegisteredEvent.class;

    }

}
