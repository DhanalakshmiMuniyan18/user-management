package com.usermanagement.dto;

import com.usermanagement.model.entity.AccessRequest.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Saravanamuthukumar S
 */
@Data
public class AccessRequestDto {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    private Status status;
    private String reason;
    private String responseMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 