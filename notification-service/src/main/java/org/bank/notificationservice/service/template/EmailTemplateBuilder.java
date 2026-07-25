package org.bank.notificationservice.service.template;

public interface EmailTemplateBuilder {

    String build(Object event);

    Class<?> supports();
}
