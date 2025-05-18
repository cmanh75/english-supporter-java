package com.service;

import com.dto.AuthResponse;
import com.dto.LoginRequest;
import com.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
} 