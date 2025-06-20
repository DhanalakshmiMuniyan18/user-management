package com.usermanagement.controller;

import com.usermanagement.dto.PermissionDto;
import com.usermanagement.service.PermissionService;
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

/**
 * @author Saravanamuthukumar S
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create a new permission", responses = {
        @ApiResponse(responseCode = "201", description = "Permission created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Permission name already exists")
    })
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody PermissionDto permissionDto) {
        PermissionDto createdPermission = permissionService.createPermission(permissionDto);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Permission found"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> getPermissionById(
        @Parameter(description = "Permission ID", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get permission by name", responses = {
        @ApiResponse(responseCode = "200", description = "Permission found"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> getPermissionByName(
        @Parameter(description = "Permission name", required = true)
        @PathVariable String name
    ) {
        return ResponseEntity.ok(permissionService.getPermissionByName(name));
    }

    @GetMapping
    @Operation(summary = "List permissions with filtering and pagination")
    public ResponseEntity<Page<PermissionDto>> getAllPermissions(
        @Parameter(description = "Search by name or description")
        @RequestParam(required = false) String search,
        
        @Parameter(description = "Pagination parameters")
        Pageable pageable
    ) {
        return ResponseEntity.ok(permissionService.getAllPermissions(search, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission details", responses = {
        @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> updatePermission(
        @Parameter(description = "Permission ID", required = true)
        @PathVariable Long id,
        
        @Valid @RequestBody PermissionDto permissionDto
    ) {
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission", responses = {
        @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Permission not found"),
        @ApiResponse(responseCode = "400", description = "Permission cannot be deleted")
    })
    public ResponseEntity<Void> deletePermission(
        @Parameter(description = "Permission ID", required = true)
        @PathVariable Long id
    ) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
} 