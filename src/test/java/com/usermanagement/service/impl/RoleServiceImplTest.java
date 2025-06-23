package com.usermanagement.service.impl;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.RoleMapper;
import com.usermanagement.model.entity.Permission;
import com.usermanagement.model.entity.Role;
import com.usermanagement.repository.PermissionRepository;
import com.usermanagement.repository.RoleRepository;
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

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleMapper roleMapper;
    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;
    private final Long roleId = 1L;
    private final String roleName = "ADMIN";
    private final Set<String> permissionNames = new HashSet<>(Arrays.asList("READ", "WRITE"));
    private final Set<Permission> permissions = new HashSet<>();

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(roleId);
        role.setName(roleName);
        role.setDescription("desc");
        role.setPermissions(new HashSet<>());
        role.setUsers(new HashSet<>());

        roleDto = new RoleDto(roleId, roleName, "desc", permissionNames);

        Permission p1 = new Permission();
        p1.setName("READ");
        Permission p2 = new Permission();
        p2.setName("WRITE");
        permissions.clear();
        permissions.add(p1);
        permissions.add(p2);
    }

    @Test
    void createRole_Success() {
        when(roleRepository.existsByName(roleName)).thenReturn(false);
        when(roleMapper.toEntity(roleDto)).thenReturn(role);
        when(permissionRepository.findByNameIn(permissionNames)).thenReturn(permissions);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.createRole(roleDto);
        assertNotNull(result);
        assertEquals(roleName, result.name());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_DuplicateName_ThrowsException() {
        when(roleRepository.existsByName(roleName)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(roleDto));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRoleById_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.getRoleById(roleId);
        assertNotNull(result);
        assertEquals(roleId, result.id());
    }

    @Test
    void getRoleById_NotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(roleId));
    }

    @Test
    void getRoleByName_Success() {
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.getRoleByName(roleName);
        assertNotNull(result);
        assertEquals(roleName, result.name());
    }

    @Test
    void getRoleByName_NotFound_ThrowsException() {
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByName(roleName));
    }

    @Test
    void getAllRoles_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(Collections.singletonList(role));
        when(roleRepository.findBySearchCriteria(any(), eq(pageable))).thenReturn(rolePage);
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        Page<RoleDto> result = roleService.getAllRoles("search", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findByNameIn(permissionNames)).thenReturn(permissions);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.updateRole(roleId, roleDto);
        assertNotNull(result);
        assertEquals(roleName, result.name());
    }

    @Test
    void updateRole_DuplicateName_ThrowsException() {
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("OLD_NAME");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByName(roleName)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> roleService.updateRole(roleId, roleDto));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void updateRole_NotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(roleId, roleDto));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_Success() {
        Role spyRole = spy(new Role());
        spyRole.setId(roleId);
        doReturn(new HashSet<com.usermanagement.model.entity.User>()).when(spyRole).getUsers();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(spyRole));
        assertDoesNotThrow(() -> roleService.deleteRole(roleId));
        verify(roleRepository).delete(spyRole);
    }

    @Test
    void deleteRole_AssignedToUsers_ThrowsException() {
        Role assignedRole = spy(new Role());
        assignedRole.setId(roleId);
        Set<com.usermanagement.model.entity.User> users = new HashSet<>();
        users.add(new com.usermanagement.model.entity.User());
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(assignedRole));
        when(assignedRole.getUsers()).thenReturn(users);
        assertThrows(IllegalStateException.class, () -> roleService.deleteRole(roleId));
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void deleteRole_NotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(roleId));
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void addPermissionsToRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findByNameIn(permissionNames)).thenReturn(permissions);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.addPermissionsToRole(roleId, permissionNames);
        assertNotNull(result);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void addPermissionsToRole_PermissionsNotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Set<Permission> foundPermissions = new HashSet<>();
        foundPermissions.add(new Permission());
        when(permissionRepository.findByNameIn(permissionNames)).thenReturn(foundPermissions);
        assertThrows(ResourceNotFoundException.class, () -> roleService.addPermissionsToRole(roleId, permissionNames));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void addPermissionsToRole_RoleNotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.addPermissionsToRole(roleId, permissionNames));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void removePermissionsFromRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findByNameIn(permissionNames)).thenReturn(permissions);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleDto);
        RoleDto result = roleService.removePermissionsFromRole(roleId, permissionNames);
        assertNotNull(result);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void removePermissionsFromRole_RoleNotFound_ThrowsException() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.removePermissionsFromRole(roleId, permissionNames));
        verify(roleRepository, never()).save(any(Role.class));
    }
} 