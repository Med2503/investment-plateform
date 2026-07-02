package org.bank.authservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.authservice.dto.request.LoginRequest;
import org.bank.authservice.dto.request.RegisterRequest;
import org.bank.authservice.dto.response.LoginResponse;
import org.bank.authservice.dto.response.ProfileResponse;
import org.bank.authservice.dto.response.RegisterResponse;
import org.bank.authservice.service.impl.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(Authentication authentication) {
        return authService.getProfile(authentication.getName());
    }
}
