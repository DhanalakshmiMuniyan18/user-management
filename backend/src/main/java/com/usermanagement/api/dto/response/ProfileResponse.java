package com.usermanagement.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<String> roles;
} 