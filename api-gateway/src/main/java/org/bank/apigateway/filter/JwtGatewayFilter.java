package org.bank.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.bank.apigateway.security.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String auth = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);


        if (auth == null || !auth.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = auth.substring(7);

        try {
            if (jwtService.isTokenExpired(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Long userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build()
            );

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}