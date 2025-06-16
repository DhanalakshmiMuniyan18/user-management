package com.usermanagement.api.service.impl;

import com.usermanagement.api.dto.request.UpdateProfileRequest;
import com.usermanagement.api.dto.response.UserProfileResponse;
import com.usermanagement.api.exception.EmailAlreadyExistsException;
import com.usermanagement.api.exception.ResourceNotFoundException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import com.usermanagement.api.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        user = userRepository.save(user);
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public void updatePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void uploadProfilePicture(String username, byte[] pictureData) {
        if (pictureData == null || pictureData.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setProfilePicture(pictureData);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (user.getProfilePicture() == null) {
            throw new ResourceNotFoundException("Profile picture not found for user: " + username);
        }

        return user.getProfilePicture();
    }

    @Override
    @Transactional
    public void deleteProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setProfilePicture(null);
        userRepository.save(user);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles().stream().map(r -> "ROLE_" + r).collect(java.util.stream.Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
} 