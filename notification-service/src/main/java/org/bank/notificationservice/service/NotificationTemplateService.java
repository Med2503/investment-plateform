package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final SpringTemplateEngine engine;
}
