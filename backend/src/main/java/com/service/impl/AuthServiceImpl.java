package com.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dto.AuthResponse;
import com.dto.LoginRequest;
import com.dto.RegisterRequest;
import com.entity.User;
import com.repository.UserRepository;
import com.service.AuthService;
import com.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthResponse.builder()
                    .message("Username already exists")
                    .build();
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthResponse.builder()
                    .message("Email already exists")
                    .build();
        }

        // Create new user
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate JWT token
        var token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .message("User registered successfully")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

            var token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .message("Login successful")
                    .build();

        } catch (Exception e) {
            return AuthResponse.builder()
                    .message("Invalid username or password")
                    .build();
        }
    }
} 