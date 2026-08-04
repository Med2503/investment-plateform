package org.bank.notificationservice.service.template;

import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.risk.RiskDecisionCreatedEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class RiskDecisionTemplateBuilder implements EmailTemplateBuilder {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String build(
            Object event
    ) {

        RiskDecisionCreatedEvent risk =
                (RiskDecisionCreatedEvent) event;

        Context context = new Context();

        context.setVariable(
                "symbol",
                risk.symbol()
        );

        context.setVariable(
                "amount",
                risk.amount()
        );
        context.setVariable(
                "status",
                risk.status()
        );

        context.setVariable(
                "reason",
                risk.reason()
        );

        return templateEngine.process(

                "risk-decision",

                context

        );

    }

    @Override
    public Class<?> supports() {
        return RiskDecisionCreatedEvent.class;
    }

}
