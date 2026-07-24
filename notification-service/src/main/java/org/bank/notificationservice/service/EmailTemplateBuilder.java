package org.bank.notificationservice.service;

public interface EmailTemplateBuilder {

    String build(Object event);
}
