package com.usermanagement.api.service.impl;

import com.usermanagement.api.dto.request.LoginRequest;
import com.usermanagement.api.dto.request.RegisterRequest;
import com.usermanagement.api.dto.response.AuthResponse;
import com.usermanagement.api.exception.EmailAlreadyExistsException;
import com.usermanagement.api.exception.ResourceNotFoundException;
import com.usermanagement.api.exception.UsernameAlreadyExistsException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.model.UserStatus;
import com.usermanagement.api.repository.UserRepository;
import com.usermanagement.api.security.JwtTokenProvider;
import com.usermanagement.api.service.AuthService;
import com.usermanagement.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(false)
                .emailVerified(false)
                .roles(Set.of("USER"))
                .build();

        userRepository.save(user);

        // TODO: Generate and send verification token
        // String token = generateVerificationToken(user);
        // emailService.sendVerificationEmail(user.getEmail(), token);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(r -> "ROLE_" + r).collect(Collectors.toList()))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        // Fetch user and check status
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalStateException("User account is deactivated. Please contact support.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(r -> "ROLE_" + r).collect(Collectors.toList()))
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList()));

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(r -> "ROLE_" + r).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        // TODO: Implement email verification logic
        // This would typically involve:
        // 1. Validating the token
        // 2. Finding the user associated with the token
        // 3. Marking the user's email as verified
        // 4. Enabling the user's account
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // TODO: Implement password reset logic
        // This would typically involve:
        // 1. Validating the token
        // 2. Finding the user associated with the token
        // 3. Updating the user's password
        // 4. Invalidating the token
    }
}
