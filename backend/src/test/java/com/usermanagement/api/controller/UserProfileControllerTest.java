package com.usermanagement.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.api.dto.request.PasswordUpdateRequest;
import com.usermanagement.api.dto.request.ProfileUpdateRequest;
import com.usermanagement.api.dto.request.UpdateProfileRequest;
import com.usermanagement.api.dto.response.UserProfileResponse;
import com.usermanagement.api.security.JwtAuthenticationFilter;
import com.usermanagement.api.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setUp() {
        userProfileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .enabled(true)
                .emailVerified(true)
                .roles(List.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("admin")
                .updatedBy("admin")
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProfile_WithValidRequest_ShouldReturnProfile() throws Exception {
        when(userProfileService.getProfile("testuser")).thenReturn(userProfileResponse);

        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateProfile_WithValidRequest_ShouldUpdateProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPhoneNumber("9876543210");

        when(userProfileService.updateProfile("testuser", request)).thenReturn(userProfileResponse);

        mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userProfileService).updateProfile("testuser", request);
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateProfile_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName(""); // Empty first name
        request.setLastName(""); // Empty last name
        request.setPhoneNumber("invalid"); // Invalid phone number

        mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userProfileService, never()).updateProfile(any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePassword_WithValidRequest_ShouldUpdatePassword() throws Exception {
        PasswordUpdateRequest request = new PasswordUpdateRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPass123!@#"); // Valid password with special character

        doNothing().when(userProfileService).updatePassword("testuser", request.getCurrentPassword(), request.getNewPassword());

        mockMvc.perform(put("/api/v1/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userProfileService).updatePassword("testuser", request.getCurrentPassword(), request.getNewPassword());
    }

    @Test
    @WithMockUser(username = "testuser")
    void uploadProfilePicture_WithValidFile_ShouldUploadPicture() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        doNothing().when(userProfileService).uploadProfilePicture("testuser", file.getBytes());

        mockMvc.perform(multipart("/api/v1/profile/picture")
                .file(file))
                .andExpect(status().isOk());

        verify(userProfileService).uploadProfilePicture("testuser", file.getBytes());
    }

    @Test
    @WithMockUser(username = "testuser")
    void uploadProfilePicture_WithEmptyData_ShouldThrowException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        doThrow(new IllegalArgumentException("File is empty"))
                .when(userProfileService).uploadProfilePicture("testuser", file.getBytes());

        mockMvc.perform(multipart("/api/v1/profile/picture")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(userProfileService).uploadProfilePicture("testuser", file.getBytes());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProfilePicture_ShouldReturnPicture() throws Exception {
        byte[] pictureData = "test image content".getBytes();
        when(userProfileService.getProfilePicture("testuser")).thenReturn(pictureData);

        mockMvc.perform(get("/api/v1/profile/picture"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(pictureData));

        verify(userProfileService).getProfilePicture("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteProfilePicture_ShouldDeletePicture() throws Exception {
        doNothing().when(userProfileService).deleteProfilePicture("testuser");

        mockMvc.perform(delete("/api/v1/profile/picture"))
                .andExpect(status().isOk());

        verify(userProfileService).deleteProfilePicture("testuser");
    }
} 