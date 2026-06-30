package org.bank.authservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.bank.authservice.dto.request.LoginRequest;
import org.bank.authservice.dto.request.RegisterRequest;
import org.bank.authservice.dto.response.AuthResponse;
import org.bank.authservice.entity.Role;
import org.bank.authservice.entity.User;
import org.bank.authservice.exception.UsernameAlreadyExistsException;
import org.bank.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthResponse register(RegisterRequest request) {

        System.out.println("register called!");

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        return new AuthResponse("user registered ");

    }


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        return new AuthResponse("Login successful!");
    }

}
