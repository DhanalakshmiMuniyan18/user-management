package com.userservice.management.rbac.controller;

import com.userservice.management.rbac.dto.PermissionDTO;
import com.userservice.management.rbac.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private PermissionDTO permissionDTO;
    private final Long permissionId = 1L;

    @BeforeEach
    void setUp() {
        permissionDTO = PermissionDTO.builder()
                .id(permissionId)
                .name("TEST_PERMISSION")
                .description("Test Permission")
                .build();
    }

    @Test
    void testCreatePermission() {
        when(permissionService.createPermission(any(PermissionDTO.class))).thenReturn(permissionDTO);

        ResponseEntity<PermissionDTO> response = permissionController.createPermission(permissionDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(permissionDTO, response.getBody());
        verify(permissionService).createPermission(permissionDTO);
    }

    @Test
    void testGetPermission() {
        when(permissionService.getPermission(permissionId)).thenReturn(permissionDTO);

        ResponseEntity<PermissionDTO> response = permissionController.getPermission(permissionId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(permissionDTO, response.getBody());
        verify(permissionService).getPermission(permissionId);
    }

    @Test
    void testGetAllPermissions() {
        List<PermissionDTO> permissions = Arrays.asList(
                permissionDTO,
                PermissionDTO.builder().id(2L).name("PERMISSION_2").build()
        );

        when(permissionService.getAllPermissions()).thenReturn(permissions);

        ResponseEntity<List<PermissionDTO>> response = permissionController.getAllPermissions();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(permissions, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(permissionService).getAllPermissions();
    }

    @Test
    void testUpdatePermission() {
        PermissionDTO updatedDTO = PermissionDTO.builder()
                .id(permissionId)
                .name("UPDATED_PERMISSION")
                .description("Updated Description")
                .build();

        when(permissionService.updatePermission(eq(permissionId), any(PermissionDTO.class)))
                .thenReturn(updatedDTO);

        ResponseEntity<PermissionDTO> response = permissionController.updatePermission(permissionId, updatedDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals("UPDATED_PERMISSION", response.getBody().getName());
        verify(permissionService).updatePermission(permissionId, updatedDTO);
    }

    @Test
    void testDeletePermission() {
        ResponseEntity<Void> response = permissionController.deletePermission(permissionId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(permissionService).deletePermission(permissionId);
    }
} 