package com.userservice.management.rbac.controller;

import com.userservice.management.rbac.dto.RoleDTO;
import com.userservice.management.rbac.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private RoleDTO roleDTO;
    private final Long roleId = 1L;

    @BeforeEach
    void setUp() {
        Set<Long> permissionIds = new HashSet<>(Arrays.asList(1L, 2L));
        roleDTO = RoleDTO.builder()
                .id(roleId)
                .name("TEST_ROLE")
                .description("Test Role")
                .permissionIds(permissionIds)
                .build();
    }

    @Test
    void testCreateRole() {
        when(roleService.createRole(any(RoleDTO.class))).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = roleController.createRole(roleDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roleDTO, response.getBody());
        verify(roleService).createRole(roleDTO);
    }

    @Test
    void testGetRole() {
        when(roleService.getRole(roleId)).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = roleController.getRole(roleId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDTO, response.getBody());
        verify(roleService).getRole(roleId);
    }

    @Test
    void testGetAllRoles() {
        List<RoleDTO> roles = Arrays.asList(
                roleDTO,
                RoleDTO.builder().id(2L).name("ROLE_2").build()
        );

        when(roleService.getAllRoles()).thenReturn(roles);

        ResponseEntity<List<RoleDTO>> response = roleController.getAllRoles();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(roleService).getAllRoles();
    }

    @Test
    void testUpdateRole() {
        RoleDTO updatedDTO = RoleDTO.builder()
                .id(roleId)
                .name("UPDATED_ROLE")
                .description("Updated Description")
                .build();

        when(roleService.updateRole(eq(roleId), any(RoleDTO.class)))
                .thenReturn(updatedDTO);

        ResponseEntity<RoleDTO> response = roleController.updateRole(roleId, updatedDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());
        assertEquals("UPDATED_ROLE", response.getBody().getName());
        verify(roleService).updateRole(roleId, updatedDTO);
    }

    @Test
    void testDeleteRole() {
        ResponseEntity<Void> response = roleController.deleteRole(roleId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService).deleteRole(roleId);
    }

    @Test
    void testAssignPermissionsToRole() {
        Set<Long> permissionIds = new HashSet<>(Arrays.asList(3L, 4L));
        RoleDTO updatedRole = RoleDTO.builder()
                .id(roleId)
                .name("TEST_ROLE")
                .permissionIds(permissionIds)
                .build();

        when(roleService.assignPermissionsToRole(eq(roleId), any())).thenReturn(updatedRole);

        ResponseEntity<RoleDTO> response = roleController.assignPermissionsToRole(roleId, permissionIds);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRole, response.getBody());
        assertEquals(permissionIds, response.getBody().getPermissionIds());
        verify(roleService).assignPermissionsToRole(roleId, permissionIds);
    }

    @Test
    void testRemovePermissionFromRole() {
        Long permissionId = 1L;
        RoleDTO updatedRole = RoleDTO.builder()
                .id(roleId)
                .name("TEST_ROLE")
                .permissionIds(new HashSet<>())
                .build();

        when(roleService.removePermissionFromRole(roleId, permissionId)).thenReturn(updatedRole);

        ResponseEntity<RoleDTO> response = roleController.removePermissionFromRole(roleId, permissionId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRole, response.getBody());
        assertTrue(response.getBody().getPermissionIds().isEmpty());
        verify(roleService).removePermissionFromRole(roleId, permissionId);
    }
} 