package com.usermanagement.api.service;

import com.usermanagement.api.dto.request.LoginRequest;
import com.usermanagement.api.dto.request.RegisterRequest;
import com.usermanagement.api.dto.response.AuthResponse;
import com.usermanagement.api.exception.EmailAlreadyExistsException;
import com.usermanagement.api.exception.UsernameAlreadyExistsException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import com.usermanagement.api.security.JwtTokenProvider;
import com.usermanagement.api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private EmailService emailService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setPhoneNumber("1234567890");

        user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password("encodedPassword")
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .roles(Set.of("USER"))
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_WithValidRequest_ShouldSucceed() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setFirstName("Test");
        request.setLastName("User");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refreshToken");

        AuthResponse response = authService.register(request);
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals(request.getUsername(), response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    @Test
    void register_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(any())).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refreshToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals(user.getUsername(), response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    @Test
    void refreshToken_WithValidToken_ShouldSucceed() {
        // Arrange
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(any())).thenReturn("testuser");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(any())).thenReturn("newAccessToken");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("newRefreshToken");

        // Act
        AuthResponse response = authService.refreshToken("validRefreshToken");

        // Assert
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals(user.getUsername(), response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }
} 