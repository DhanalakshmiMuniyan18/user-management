package com.usermanagement.api.controller;

import com.usermanagement.api.dto.request.UpdateProfileRequest;
import com.usermanagement.api.dto.request.PasswordUpdateRequest;
import com.usermanagement.api.dto.response.UserProfileResponse;
import com.usermanagement.api.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "User Profile", description = "User profile management APIs")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Get user profile")
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userProfileService.getProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Update user profile")
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userDetails.getUsername(), request));
    }

    @Operation(summary = "Update user password")
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest request) {
        userProfileService.updatePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload profile picture")
    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {
        userProfileService.uploadProfilePicture(userDetails.getUsername(), file.getBytes());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get profile picture")
    @GetMapping("/picture")
    public ResponseEntity<byte[]> getProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        byte[] imageData = userProfileService.getProfilePicture(userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @Operation(summary = "Delete profile picture")
    @DeleteMapping("/picture")
    public ResponseEntity<Void> deleteProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        userProfileService.deleteProfilePicture(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
} 