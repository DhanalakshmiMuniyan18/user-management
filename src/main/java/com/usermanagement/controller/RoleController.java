package com.usermanagement.controller;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.dto.StandardResponse;
import com.usermanagement.service.RoleService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for role management.
 * <p>
 * All endpoints are secured and audited.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management endpoints")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new role", responses = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    @Timed(value = "role.create", description = "Time taken to create a role")
    public CompletableFuture<ResponseEntity<StandardResponse<RoleDto>>> createRole(
            @Valid @RequestBody RoleDto roleDto) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Creating role: {}", roleDto.name());
        return CompletableFuture.supplyAsync(() -> {
            RoleDto createdRole = roleService.createRole(roleDto);
            log.info("Role created: {}", createdRole.name());
            return ResponseEntity.status(201)
                    .body(StandardResponse.success(createdRole, correlationId));
        }).whenComplete((r, t) -> MDC.clear());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    @Operation(summary = "Get role by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @Cacheable(value = "roles", key = "#id")
    public ResponseEntity<StandardResponse<RoleDto>> getRoleById(
            @Parameter(description = "Role ID", required = true)
            @PathVariable Long id) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Fetching role by ID: {}", id);
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(StandardResponse.success(role, correlationId));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    @Operation(summary = "Get role by name", responses = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @Cacheable(value = "roles", key = "#name")
    public ResponseEntity<StandardResponse<RoleDto>> getRoleByName(
            @Parameter(description = "Role name", required = true)
            @PathVariable String name) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Fetching role by name: {}", name);
        RoleDto role = roleService.getRoleByName(name);
        return ResponseEntity.ok(StandardResponse.success(role, correlationId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    @Operation(summary = "List roles with filtering and pagination")
    @Cacheable(value = "roles", key = "#search + '-' + #pageable.pageNumber")
    public ResponseEntity<StandardResponse<Page<RoleDto>>> getAllRoles(
            @Parameter(description = "Search by name or description")
            @RequestParam(required = false) String search,
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Listing roles with search: {}", search);
        Page<RoleDto> roles = roleService.getAllRoles(search, pageable);
        return ResponseEntity.ok(StandardResponse.success(roles, correlationId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role details", responses = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> updateRole(
        @Parameter(description = "Role ID", required = true)
        @PathVariable Long id,
        
        @Valid @RequestBody RoleDto roleDto
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", responses = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Role cannot be deleted")
    })
    public ResponseEntity<Void> deleteRole(
        @Parameter(description = "Role ID", required = true)
        @PathVariable Long id
    ) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "Add permissions to role", responses = {
        @ApiResponse(responseCode = "200", description = "Permissions added successfully"),
        @ApiResponse(responseCode = "404", description = "Role or permissions not found")
    })
    public ResponseEntity<RoleDto> addPermissionsToRole(
        @Parameter(description = "Role ID", required = true)
        @PathVariable Long id,
        
        @Parameter(description = "Permission names to add", required = true)
        @RequestBody Set<String> permissionNames
    ) {
        return ResponseEntity.ok(roleService.addPermissionsToRole(id, permissionNames));
    }

    @DeleteMapping("/{id}/permissions")
    @Operation(summary = "Remove permissions from role", responses = {
        @ApiResponse(responseCode = "200", description = "Permissions removed successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> removePermissionsFromRole(
        @Parameter(description = "Role ID", required = true)
        @PathVariable Long id,
        
        @Parameter(description = "Permission names to remove", required = true)
        @RequestBody Set<String> permissionNames
    ) {
        return ResponseEntity.ok(roleService.removePermissionsFromRole(id, permissionNames));
    }
}