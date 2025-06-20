package com.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Saravanamuthukumar S
 */
@Data
public class RoleDto {
    
    private Long id;
    
    @NotBlank(message = "Role name is required")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    private String name;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    private Set<String> permissionNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 