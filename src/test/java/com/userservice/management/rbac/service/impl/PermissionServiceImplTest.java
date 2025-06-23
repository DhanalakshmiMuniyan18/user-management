package com.userservice.management.rbac.service.impl;

import com.userservice.management.rbac.dto.PermissionDTO;
import com.userservice.management.rbac.exception.DuplicateResourceException;
import com.userservice.management.rbac.exception.ResourceNotFoundException;
import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;
    private PermissionDTO permissionDTO;
    private final Long permissionId = 1L;
    private final String permissionName = "TEST_PERMISSION";

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(permissionId)
                .name(permissionName)
                .description("Test Permission Description")
                .build();

        permissionDTO = PermissionDTO.builder()
                .id(permissionId)
                .name(permissionName)
                .description("Test Permission Description")
                .build();
    }

    @Test
    void createPermission_Success() {
        when(permissionRepository.existsByName(permissionName)).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        PermissionDTO result = permissionService.createPermission(permissionDTO);

        assertNotNull(result);
        assertEquals(permissionDTO.getName(), result.getName());
        assertEquals(permissionDTO.getDescription(), result.getDescription());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void createPermission_DuplicateName_ThrowsException() {
        when(permissionRepository.existsByName(permissionName)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> permissionService.createPermission(permissionDTO));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void getPermission_Success() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));

        PermissionDTO result = permissionService.getPermission(permissionId);

        assertNotNull(result);
        assertEquals(permissionDTO.getId(), result.getId());
        assertEquals(permissionDTO.getName(), result.getName());
        assertEquals(permissionDTO.getDescription(), result.getDescription());
    }

    @Test
    void getPermission_NotFound_ThrowsException() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.getPermission(permissionId));
    }

    @Test
    void getAllPermissions_Success() {
        List<Permission> permissions = Collections.singletonList(permission);
        when(permissionRepository.findAll()).thenReturn(permissions);

        List<PermissionDTO> result = permissionService.getAllPermissions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(permissionDTO.getName(), result.get(0).getName());
        assertEquals(permissionDTO.getDescription(), result.get(0).getDescription());
    }

    @Test
    void updatePermission_Success() {
        Permission existingPermission = Permission.builder()
                .id(permissionId)
                .name("OLD_NAME")
                .build();

        PermissionDTO updateDTO = PermissionDTO.builder()
                .name("NEW_NAME")
                .description("New Description")
                .build();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.existsByName("NEW_NAME")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(existingPermission);

        PermissionDTO result = permissionService.updatePermission(permissionId, updateDTO);

        assertNotNull(result);
        assertEquals("NEW_NAME", result.getName());
        assertEquals("New Description", result.getDescription());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void updatePermission_NotFound_ThrowsException() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.updatePermission(permissionId, permissionDTO));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void updatePermission_DuplicateName_ThrowsException() {
        Permission existingPermission = Permission.builder()
                .id(permissionId)
                .name("OLD_NAME")
                .build();

        PermissionDTO updateDTO = PermissionDTO.builder()
                .name("NEW_NAME")
                .build();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.existsByName("NEW_NAME")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> permissionService.updatePermission(permissionId, updateDTO));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void deletePermission_Success() {
        when(permissionRepository.existsById(permissionId)).thenReturn(true);

        assertDoesNotThrow(() -> permissionService.deletePermission(permissionId));

        verify(permissionRepository).deleteById(permissionId);
    }

    @Test
    void deletePermission_NotFound_ThrowsException() {
        when(permissionRepository.existsById(permissionId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.deletePermission(permissionId));

        verify(permissionRepository, never()).deleteById(any());
    }

    @Test
    void getPermissionsByIds_Success() {
        Set<Long> ids = Collections.singleton(permissionId);
        when(permissionRepository.findByIdIn(ids)).thenReturn(Collections.singleton(permission));

        Set<PermissionDTO> result = permissionService.getPermissionsByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        PermissionDTO resultDTO = result.iterator().next();
        assertEquals(permissionDTO.getId(), resultDTO.getId());
        assertEquals(permissionDTO.getName(), resultDTO.getName());
        assertEquals(permissionDTO.getDescription(), resultDTO.getDescription());
    }

    @Test
    void getPermissionsByIds_EmptyResult() {
        Set<Long> ids = Collections.singleton(permissionId);
        when(permissionRepository.findByIdIn(ids)).thenReturn(Collections.emptySet());

        Set<PermissionDTO> result = permissionService.getPermissionsByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
} 