package com.usermanagement.controller;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Saravanamuthukumar S
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", responses = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleDto createdRole = roleService.createRole(roleDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> getRoleById(
        @Parameter(description = "Role ID", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get role by name", responses = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> getRoleByName(
        @Parameter(description = "Role name", required = true)
        @PathVariable String name
    ) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }

    @GetMapping
    @Operation(summary = "List roles with filtering and pagination")
    public ResponseEntity<Page<RoleDto>> getAllRoles(
        @Parameter(description = "Search by name or description")
        @RequestParam(required = false) String search,
        
        @Parameter(description = "Pagination parameters")
        Pageable pageable
    ) {
        return ResponseEntity.ok(roleService.getAllRoles(search, pageable));
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