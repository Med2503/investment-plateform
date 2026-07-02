package org.bank.authservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.bank.authservice.dto.request.LoginRequest;
import org.bank.authservice.dto.request.RegisterRequest;
import org.bank.authservice.dto.response.LoginResponse;
import org.bank.authservice.dto.response.ProfileResponse;
import org.bank.authservice.dto.response.RegisterResponse;
import org.bank.authservice.entity.Role;
import org.bank.authservice.entity.User;
import org.bank.authservice.exception.UsernameAlreadyExistsException;
import org.bank.authservice.repository.UserRepository;
import org.bank.authservice.security.jwt.JwtProperties;
import org.bank.authservice.security.jwt.JwtService;
import org.bank.authservice.security.service.CustomUserDetailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;


    public RegisterResponse register(RegisterRequest request) {

        System.out.println("register called!");

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        User saveUser = userRepository.save(user);

        return new RegisterResponse(
                saveUser.getId(),
                saveUser.getUsername(),
                "User registered successfully"
        );

    }


    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("iss", "auth-service");

        UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getUsername());

        String token = jwtService.generateToken(claims, userDetails);


        return new LoginResponse(
                token,
                "Bearer",
                jwtProperties.getExpiration()
        );
    }

    public ProfileResponse getProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

    }

}
