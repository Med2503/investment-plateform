package org.bank.apigateway.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {
    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();

            if (path.contains("/customers")) {
                String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
                return Mono.just("user:" + userId);
            }

            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            return Mono.just("ip:" + ip);
        };
    }
}
