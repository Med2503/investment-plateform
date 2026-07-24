package org.bank.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.account.WithdrawCompletedEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@RequiredArgsConstructor
public class WithdrawEmailTemplateBuilder
        implements EmailTemplateBuilder {


    private final SpringTemplateEngine templateEngine;


    @Override
    public String build(Object event) {


        WithdrawCompletedEvent withdraw =
                (WithdrawCompletedEvent) event;


        Context context = new Context();


        context.setVariable(
                "amount",
                withdraw.amount()
        );


        context.setVariable(
                "accountId",
                withdraw.accountId()
        );


        return templateEngine.process(
                "emails/withdraw",
                context
        );

    }


    @Override
    public Class<?> supports() {

        return WithdrawCompletedEvent.class;

    }

}