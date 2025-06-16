package com.usermanagement.api.service;

import com.usermanagement.api.dto.request.LoginRequest;
import com.usermanagement.api.dto.request.RegisterRequest;
import com.usermanagement.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void verifyEmail(String token);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
} 