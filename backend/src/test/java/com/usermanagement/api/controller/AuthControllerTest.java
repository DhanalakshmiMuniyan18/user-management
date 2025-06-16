package com.usermanagement.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.api.dto.request.LoginRequest;
import com.usermanagement.api.dto.request.RegisterRequest;
import com.usermanagement.api.dto.response.AuthResponse;
import com.usermanagement.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Password123!");
        authResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username("testuser")
                .roles(List.of("ROLE_USER"))
                .build();
    }

    @Test
    void register_WithValidRequest_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123!@");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("1234567890");

        when(authService.register(any())).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService).register(any());
    }

    @Test
    void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("te"); // Too short
        request.setEmail("invalid-email"); // Invalid email
        request.setPassword("weak"); // Too weak
        request.setFirstName(""); // Empty first name
        request.setLastName(""); // Empty last name

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors").exists());

        verify(authService, never()).register(any());
    }

    @Test
    void login_WithValidRequest_ShouldReturnOk() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnAuthResponse() throws Exception {
        when(authService.refreshToken(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", "validRefreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService).refreshToken("validRefreshToken");
    }

    @Test
    void verifyEmail_WithValidToken_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify-email")
                .param("token", "validToken"))
                .andExpect(status().isOk());

        verify(authService).verifyEmail("validToken");
    }

    @Test
    void requestPasswordReset_WithValidEmail_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .param("email", "test@example.com"))
                .andExpect(status().isOk());

        verify(authService).requestPasswordReset("test@example.com");
    }

    @Test
    void resetPassword_WithValidToken_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .param("token", "validToken")
                .param("newPassword", "NewPassword123!"))
                .andExpect(status().isOk());

        verify(authService).resetPassword("validToken", "NewPassword123!");
    }
} 