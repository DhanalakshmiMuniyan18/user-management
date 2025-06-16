package com.usermanagement.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;

import java.util.Set;

@Data
@Builder
public class RoleDTO {
    private Long id;
    
    @NotBlank(message = "Role name is required")
    private String name;
    
    private String description;
    private Set<String> permissions;
} 