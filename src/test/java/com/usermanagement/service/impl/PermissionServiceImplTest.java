package com.usermanagement.service.impl;

import com.usermanagement.dto.PermissionDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.PermissionMapper;
import com.usermanagement.model.entity.Permission;
import com.usermanagement.model.entity.Role;
import com.usermanagement.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;
    private PermissionDto permissionDto;
    private final Long permissionId = 1L;
    private final String permissionName = "TEST_PERMISSION";

    @BeforeEach
    void setUp() {
        permission = new Permission();
        permission.setId(permissionId);
        permission.setName(permissionName);
        permission.setDescription("Test Permission Description");

        permissionDto = new PermissionDto();
        permissionDto.setId(permissionId);
        permissionDto.setName(permissionName);
        permissionDto.setDescription("Test Permission Description");
    }

    @Test
    void createPermission_Success() {
        when(permissionRepository.existsByName(permissionName)).thenReturn(false);
        when(permissionMapper.toEntity(permissionDto)).thenReturn(permission);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        PermissionDto result = permissionService.createPermission(permissionDto);

        assertNotNull(result);
        assertEquals(permissionDto.getName(), result.getName());
        assertEquals(permissionDto.getDescription(), result.getDescription());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void createPermission_DuplicateName_ThrowsException() {
        when(permissionRepository.existsByName(permissionName)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> permissionService.createPermission(permissionDto));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void getPermissionById_Success() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        PermissionDto result = permissionService.getPermissionById(permissionId);

        assertNotNull(result);
        assertEquals(permissionDto.getId(), result.getId());
        assertEquals(permissionDto.getName(), result.getName());
    }

    @Test
    void getPermissionById_NotFound_ThrowsException() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.getPermissionById(permissionId));
    }

    @Test
    void getPermissionByName_Success() {
        when(permissionRepository.findByName(permissionName)).thenReturn(Optional.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        PermissionDto result = permissionService.getPermissionByName(permissionName);

        assertNotNull(result);
        assertEquals(permissionDto.getName(), result.getName());
    }

    @Test
    void getPermissionByName_NotFound_ThrowsException() {
        when(permissionRepository.findByName(permissionName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.getPermissionByName(permissionName));
    }

    @Test
    void getAllPermissions_Success() {
        List<Permission> permissions = Collections.singletonList(permission);
        Page<Permission> permissionPage = new PageImpl<>(permissions);
        Page<PermissionDto> expectedDtoPage = new PageImpl<>(Collections.singletonList(permissionDto));

        Pageable pageable = PageRequest.of(0, 10);
        String search = "test";

        when(permissionRepository.findBySearchCriteria(search, pageable)).thenReturn(permissionPage);
        when(permissionMapper.toDto(permission)).thenReturn(permissionDto);

        Page<PermissionDto> result = permissionService.getAllPermissions(search, pageable);

        assertNotNull(result);
        assertEquals(expectedDtoPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedDtoPage.getContent().get(0).getName(), result.getContent().get(0).getName());
    }

    @Test
    void updatePermission_Success() {
        Permission existingPermission = new Permission();
        existingPermission.setId(permissionId);
        existingPermission.setName("OLD_NAME");

        PermissionDto updateDto = new PermissionDto();
        updateDto.setName("NEW_NAME");
        updateDto.setDescription("New Description");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.existsByName("NEW_NAME")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(existingPermission);
        when(permissionMapper.toDto(existingPermission)).thenReturn(updateDto);

        PermissionDto result = permissionService.updatePermission(permissionId, updateDto);

        assertNotNull(result);
        assertEquals("NEW_NAME", result.getName());
        assertEquals("New Description", result.getDescription());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void updatePermission_NotFound_ThrowsException() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.updatePermission(permissionId, permissionDto));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void updatePermission_DuplicateName_ThrowsException() {
        Permission existingPermission = new Permission();
        existingPermission.setId(permissionId);
        existingPermission.setName("OLD_NAME");

        PermissionDto updateDto = new PermissionDto();
        updateDto.setName("NEW_NAME");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.existsByName("NEW_NAME")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> permissionService.updatePermission(permissionId, updateDto));

        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void deletePermission_Success() {
        Permission permissionToDelete = new Permission();
        permissionToDelete.setId(permissionId);
        permissionToDelete.setRoles(new HashSet<>());

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permissionToDelete));

        assertDoesNotThrow(() -> permissionService.deletePermission(permissionId));

        verify(permissionRepository).delete(permissionToDelete);
    }

    @Test
    void deletePermission_NotFound_ThrowsException() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> permissionService.deletePermission(permissionId));

        verify(permissionRepository, never()).delete(any(Permission.class));
    }

    @Test
    void deletePermission_WithAssignedRoles_ThrowsException() {
        Permission permissionWithRoles = new Permission();
        permissionWithRoles.setId(permissionId);
        Set<Role> roles = new HashSet<>();
        roles.add(new Role());
        permissionWithRoles.setRoles(roles);

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permissionWithRoles));

        assertThrows(IllegalStateException.class,
                () -> permissionService.deletePermission(permissionId));

        verify(permissionRepository, never()).delete(any(Permission.class));
    }
} 