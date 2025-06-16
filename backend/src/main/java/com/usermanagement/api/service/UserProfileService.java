package com.usermanagement.api.service;

import com.usermanagement.api.dto.request.UpdateProfileRequest;
import com.usermanagement.api.dto.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getProfile(String username);
    UserProfileResponse updateProfile(String username, UpdateProfileRequest request);
    void updatePassword(String username, String currentPassword, String newPassword);
    void uploadProfilePicture(String username, byte[] imageData);
    byte[] getProfilePicture(String username);
    void deleteProfilePicture(String username);
} 