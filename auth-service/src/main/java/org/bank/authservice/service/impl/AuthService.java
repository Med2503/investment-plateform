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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;


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
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtService.generateToken(user);


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
