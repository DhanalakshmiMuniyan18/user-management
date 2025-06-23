package com.userservice.management.rbac.service;

import com.userservice.management.rbac.dto.RoleDTO;
import com.userservice.management.rbac.exception.DuplicateResourceException;
import com.userservice.management.rbac.exception.ResourceNotFoundException;
import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.model.Role;
import com.userservice.management.rbac.repository.PermissionRepository;
import com.userservice.management.rbac.repository.RoleRepository;
import com.userservice.management.rbac.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RoleService.
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDTO roleDTO;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(1L)
                .name("READ_USER")
                .description("Can read user data")
                .build();

        role = Role.builder()
                .id(1L)
                .name("USER")
                .description("Basic user role")
                .permissions(new HashSet<>(Collections.singletonList(permission)))
                .build();

        roleDTO = RoleDTO.builder()
                .id(1L)
                .name("USER")
                .description("Basic user role")
                .permissionIds(new HashSet<>(Collections.singletonList(1L)))
                .build();
    }

    @Test
    void createRole_Success() {
        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(permissionRepository.findByIdIn(any())).thenReturn(Set.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDTO result = roleService.createRole(roleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(roleDTO.getName());
        assertThat(result.getDescription()).isEqualTo(roleDTO.getDescription());
        assertThat(result.getPermissionIds()).containsExactlyInAnyOrderElementsOf(roleDTO.getPermissionIds());

        verify(roleRepository).existsByName(roleDTO.getName());
        verify(permissionRepository).findByIdIn(roleDTO.getPermissionIds());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_DuplicateName_ThrowsException() {
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(roleDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Role already exists with name");

        verify(roleRepository).existsByName(roleDTO.getName());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRole_Success() {
        when(roleRepository.findByIdWithPermissions(anyLong())).thenReturn(Optional.of(role));

        RoleDTO result = roleService.getRole(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(role.getId());
        assertThat(result.getName()).isEqualTo(role.getName());
        assertThat(result.getDescription()).isEqualTo(role.getDescription());

        verify(roleRepository).findByIdWithPermissions(1L);
    }

    @Test
    void getRole_NotFound_ThrowsException() {
        when(roleRepository.findByIdWithPermissions(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRole(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found");

        verify(roleRepository).findByIdWithPermissions(1L);
    }

    @Test
    void assignPermissionsToRole_Success() {
        when(roleRepository.findByIdWithPermissions(anyLong())).thenReturn(Optional.of(role));
        when(permissionRepository.findByIdIn(any())).thenReturn(Set.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Set<Long> permissionIds = Set.of(1L);
        RoleDTO result = roleService.assignPermissionsToRole(1L, permissionIds);

        assertThat(result).isNotNull();
        assertThat(result.getPermissionIds()).contains(1L);

        verify(roleRepository).findByIdWithPermissions(1L);
        verify(permissionRepository).findByIdIn(permissionIds);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_WithMultiplePermissions_Success() {
        Permission permission2 = Permission.builder()
                .id(2L)
                .name("WRITE_USER")
                .description("Can write user data")
                .build();

        RoleDTO newRoleDTO = RoleDTO.builder()
                .name("ADMIN")
                .description("Admin role with multiple permissions")
                .permissionIds(Set.of(1L, 2L))
                .build();

        Role savedRole = Role.builder()
                .id(2L)
                .name("ADMIN")
                .description("Admin role with multiple permissions")
                .permissions(new HashSet<>(Arrays.asList(permission, permission2)))
                .build();

        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(permissionRepository.findByIdIn(Set.of(1L, 2L))).thenReturn(Set.of(permission, permission2));
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        RoleDTO result = roleService.createRole(newRoleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ADMIN");
        assertThat(result.getDescription()).isEqualTo("Admin role with multiple permissions");
        assertThat(result.getPermissionIds()).containsExactlyInAnyOrder(1L, 2L);

        verify(roleRepository).existsByName("ADMIN");
        verify(permissionRepository).findByIdIn(Set.of(1L, 2L));
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_WithBlankName_ShouldFailValidation() {
        RoleDTO invalidRoleDTO = RoleDTO.builder()
                .name("")
                .description("Role with blank name")
                .permissionIds(Set.of(1L))
                .build();

        // Simulate validation exception (if using a validator, e.g., javax.validation)
        assertThatThrownBy(() -> roleService.createRole(invalidRoleDTO))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Role name is required");
    }
} 