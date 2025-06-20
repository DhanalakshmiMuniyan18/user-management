package com.usermanagement.controller;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.service.AuditLogService;
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
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log management endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping
    @Operation(summary = "Create a new audit log entry", responses = {
        @ApiResponse(responseCode = "201", description = "Audit log created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AuditLogDto> createLog(@Valid @RequestBody AuditLogDto auditLogDto) {
        AuditLogDto created = auditLogService.createLog(auditLogDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Audit log found"),
        @ApiResponse(responseCode = "404", description = "Audit log not found")
    })
    public ResponseEntity<AuditLogDto> getLogById(
        @Parameter(description = "Audit log ID", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(auditLogService.getLogById(id));
    }

    @GetMapping
    @Operation(summary = "List audit logs with filtering and pagination")
    public ResponseEntity<Page<AuditLogDto>> getAllLogs(
        @Parameter(description = "Search by user or action")
        @RequestParam(required = false) String search,
        
        @Parameter(description = "Pagination parameters")
        Pageable pageable
    ) {
        return ResponseEntity.ok(auditLogService.getAllLogs(search, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete audit log entry", responses = {
        @ApiResponse(responseCode = "204", description = "Audit log deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Audit log not found")
    })
    public ResponseEntity<Void> deleteLog(
        @Parameter(description = "Audit log ID", required = true)
        @PathVariable Long id
    ) {
        auditLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
} 