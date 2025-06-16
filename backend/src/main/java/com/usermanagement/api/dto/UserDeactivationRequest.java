package com.usermanagement.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeactivationRequest {
    @NotBlank(message = "Deactivation reason is required")
    private String reason;
}

