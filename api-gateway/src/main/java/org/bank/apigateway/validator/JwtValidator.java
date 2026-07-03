package org.bank.apigateway.validator;


import lombok.RequiredArgsConstructor;
import org.bank.apigateway.security.JwtService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtService jwtService;


    public boolean isValid(String token) {
        try {
            return jwtService.isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
