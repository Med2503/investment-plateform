package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.risk.RiskAlertEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@RequiredArgsConstructor
public class RiskAlertEmailTemplateBuilder
        implements EmailTemplateBuilder {



    private final SpringTemplateEngine templateEngine;



    @Override
    public String build(Object event) {


        RiskAlertEvent risk =
                (RiskAlertEvent) event;


        Context context = new Context();


        context.setVariable(
                "riskLevel",
                risk.riskLevel()
        );


        context.setVariable(
                "message",
                risk.message()
        );

        context.setVariable(
                "exposureAmount",
                risk.exposureAmount()
        );


        return templateEngine.process(
                "emails/risk-alert",
                context
        );

    }



    @Override
    public Class<?> supports() {

        return RiskAlertEvent.class;

    }

}