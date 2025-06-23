package com.userservice.management.rbac.controller;

import com.userservice.management.rbac.dto.PermissionDTO;
import com.userservice.management.rbac.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing permissions.
 * @author Saravanamuthukumar S
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create a new permission")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        return new ResponseEntity<>(permissionService.createPermission(permissionDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a permission by ID")
    public ResponseEntity<PermissionDTO> getPermission(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermission(id));
    }

    @GetMapping
    @Operation(summary = "Get all permissions")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a permission")
    public ResponseEntity<PermissionDTO> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
} 