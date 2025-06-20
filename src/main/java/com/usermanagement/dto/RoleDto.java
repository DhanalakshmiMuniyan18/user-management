package com.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * Role Data Transfer Object.
 */
@Schema(description = "Role DTO")
public record RoleDto(
        @Schema(description = "Role ID", example = "1")
        Long id,

        @NotBlank
        @Size(min = 3, max = 50)
        @Schema(description = "Role name", example = "ADMIN")
        String name,

        @Schema(description = "Role description", example = "Administrator role")
        String description,

        @Schema(description = "Permissions assigned to the role")
        Set<String> permissions
) {} 