package com.usermanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Saravanamuthukumar S
 */
@Data
public class AuditLogDto {
    private Long id;
    @NotNull(message = "User ID is required")
    private Long userId;
    private String action;
    private String details;
    private LocalDateTime createdAt;
} 