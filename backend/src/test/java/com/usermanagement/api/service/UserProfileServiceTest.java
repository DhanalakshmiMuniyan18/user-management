package com.usermanagement.api.service;

import com.usermanagement.api.dto.request.UpdateProfileRequest;
import com.usermanagement.api.dto.response.UserProfileResponse;
import com.usermanagement.api.exception.EmailAlreadyExistsException;
import com.usermanagement.api.exception.ResourceNotFoundException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import com.usermanagement.api.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private User user;
    private UpdateProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .enabled(true)
                .emailVerified(true)
                .roles(Set.of("USER"))
                .build();

        updateRequest = new UpdateProfileRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhoneNumber("0987654321");
    }

    @Test
    void getProfile_WithValidUsername_ShouldReturnProfile() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        UserProfileResponse response = userProfileService.getProfile("testuser");

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(user.isEnabled(), response.isEnabled());
        assertEquals(user.isEmailVerified(), response.isEmailVerified());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    @Test
    void getProfile_WithInvalidUsername_ShouldThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.getProfile("nonexistent"));
    }

    @Test
    void updateProfile_WithValidRequest_ShouldSucceed() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        UserProfileResponse response = userProfileService.updateProfile("testuser", updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getFirstName(), response.getFirstName());
        assertEquals(updateRequest.getLastName(), response.getLastName());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getPhoneNumber(), response.getPhoneNumber());
    }

    @Test
    void updateProfile_WithExistingEmail_ShouldThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userProfileService.updateProfile("testuser", updateRequest));
    }

    @Test
    void updatePassword_WithValidCurrentPassword_ShouldSucceed() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");

        assertDoesNotThrow(() -> userProfileService.updatePassword("testuser", "currentPassword", "newPassword"));
        verify(userRepository).save(any());
    }

    @Test
    void updatePassword_WithInvalidCurrentPassword_ShouldThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userProfileService.updatePassword("testuser", "wrongPassword", "newPassword"));
    }

    @Test
    void uploadProfilePicture_WithValidData_ShouldSavePicture() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        byte[] imageData = "test image data".getBytes();
        assertDoesNotThrow(() -> userProfileService.uploadProfilePicture("testuser", imageData));
        verify(userRepository).save(any());
    }

    @Test
    void uploadProfilePicture_WithEmptyData_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
                () -> userProfileService.uploadProfilePicture("testuser", new byte[0]));
    }

    @Test
    void getProfilePicture_WithExistingPicture_ShouldReturnPicture() {
        byte[] imageData = "test image data".getBytes();
        user.setProfilePicture(imageData);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        byte[] result = userProfileService.getProfilePicture("testuser");
        assertArrayEquals(imageData, result);
    }

    @Test
    void getProfilePicture_WithNoPicture_ShouldThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.getProfilePicture("testuser"));
    }

    @Test
    void deleteProfilePicture_WithExistingPicture_ShouldDeletePicture() {
        user.setProfilePicture("test image data".getBytes());
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userProfileService.deleteProfilePicture("testuser"));
        verify(userRepository).save(any());
    }
} 