package org.bank.riskservice.config;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRequestInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            /*
            on vas recupérer jwt depuis SecurityContextHolder => String token = ....

            puis

            template.header("Authorization","Bearer " + token);

            // cette classe seras configuré quand on voudras sécuriser
            // la communication inter-services
             */
        };
    }
}
