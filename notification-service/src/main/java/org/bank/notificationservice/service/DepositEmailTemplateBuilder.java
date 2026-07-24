package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.account.DepositCompletedEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@RequiredArgsConstructor
public class DepositEmailTemplateBuilder
        implements EmailTemplateBuilder {


    private final SpringTemplateEngine templateEngine;



    @Override
    public String build(Object event) {


        DepositCompletedEvent deposit =
                (DepositCompletedEvent) event;


        Context context = new Context();


        context.setVariable(
                "amount",
                deposit.amount()
        );


        context.setVariable(
                "accountId",
                deposit.accountId()
        );


        return templateEngine.process(
                "emails/deposit",
                context
        );

    }



    @Override
    public Class<?> supports() {

        return DepositCompletedEvent.class;

    }

}
