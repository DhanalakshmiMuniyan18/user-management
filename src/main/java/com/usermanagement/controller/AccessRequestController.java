package com.usermanagement.controller;

import com.usermanagement.dto.AccessRequestDto;
import com.usermanagement.service.AccessRequestService;
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
@RequestMapping("/access-requests")
@RequiredArgsConstructor
@Tag(name = "Access Requests", description = "Access request management endpoints")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new access request", responses = {
        @ApiResponse(responseCode = "201", description = "Access request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AccessRequestDto> createRequest(@Valid @RequestBody AccessRequestDto requestDto) {
        AccessRequestDto created = accessRequestService.createRequest(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Get access request by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Access request found"),
        @ApiResponse(responseCode = "404", description = "Access request not found")
    })
    public ResponseEntity<AccessRequestDto> getRequestById(
        @Parameter(description = "Access request ID", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(accessRequestService.getRequestById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List access requests with filtering and pagination")
    public ResponseEntity<Page<AccessRequestDto>> getAllRequests(
        @Parameter(description = "Search by user or role")
        @RequestParam(required = false) String search,
        
        @Parameter(description = "Filter by status")
        @RequestParam(required = false) String status,
        
        @Parameter(description = "Pagination parameters")
        Pageable pageable
    ) {
        return ResponseEntity.ok(accessRequestService.getAllRequests(search, status, pageable));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve access request", responses = {
        @ApiResponse(responseCode = "200", description = "Access request approved"),
        @ApiResponse(responseCode = "404", description = "Access request not found")
    })
    public ResponseEntity<AccessRequestDto> approveRequest(
        @Parameter(description = "Access request ID", required = true)
        @PathVariable Long id,
        
        @RequestBody(required = false) String responseMessage
    ) {
        return ResponseEntity.ok(accessRequestService.approveRequest(id, responseMessage));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject access request", responses = {
        @ApiResponse(responseCode = "200", description = "Access request rejected"),
        @ApiResponse(responseCode = "404", description = "Access request not found")
    })
    public ResponseEntity<AccessRequestDto> rejectRequest(
        @Parameter(description = "Access request ID", required = true)
        @PathVariable Long id,
        
        @RequestBody(required = false) String responseMessage
    ) {
        return ResponseEntity.ok(accessRequestService.rejectRequest(id, responseMessage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete access request", responses = {
        @ApiResponse(responseCode = "204", description = "Access request deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Access request not found")
    })
    public ResponseEntity<Void> deleteRequest(
        @Parameter(description = "Access request ID", required = true)
        @PathVariable Long id
    ) {
        accessRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
} 