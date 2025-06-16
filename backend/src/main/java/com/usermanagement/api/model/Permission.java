package com.usermanagement.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "permissions")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Resource is required")
    @Size(max = 50, message = "Resource must not exceed 50 characters")
    @Column(nullable = false)
    private String resource;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(CREATE|READ|UPDATE|DELETE)$", message = "Action must be one of: CREATE, READ, UPDATE, DELETE")
    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    public void validate() {
        if (name == null || name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters");
        }
        if (resource == null || resource.isEmpty() || resource.length() > 50) {
            throw new IllegalArgumentException("Resource must be between 1 and 50 characters");
        }
        if (action == null || !(action.equals("CREATE") || action.equals("READ") || action.equals("UPDATE") || action.equals("DELETE"))) {
            throw new IllegalArgumentException("Action must be one of: CREATE, READ, UPDATE, DELETE");
        }
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Description must not exceed 255 characters");
        }
    }
} 