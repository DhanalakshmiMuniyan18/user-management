package com.userservice.management.rbac.service.impl;

import com.userservice.management.rbac.dto.RoleDTO;
import com.userservice.management.rbac.exception.DuplicateResourceException;
import com.userservice.management.rbac.exception.ResourceNotFoundException;
import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.model.Role;
import com.userservice.management.rbac.repository.PermissionRepository;
import com.userservice.management.rbac.repository.RoleRepository;
import com.userservice.management.rbac.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the RoleService interface.
 * @author Saravanamuthukumar S
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleDTO.getName() == null || roleDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new DuplicateResourceException("Role", "name", roleDTO.getName());
        }

        Role role = Role.builder()
                .name(roleDTO.getName())
                .description(roleDTO.getDescription())
                .build();

        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByIdIn(roleDTO.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return mapToDTO(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRole(Long id) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return mapToDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        if (!role.getName().equals(roleDTO.getName()) &&
                roleRepository.existsByName(roleDTO.getName())) {
            throw new DuplicateResourceException("Role", "name", roleDTO.getName());
        }

        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        if (roleDTO.getPermissionIds() != null) {
            Set<Permission> permissions = permissionRepository.findByIdIn(roleDTO.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return mapToDTO(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public RoleDTO assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Set<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new ResourceNotFoundException("One or more permissions not found");
        }

        role.getPermissions().addAll(permissions);
        Role updatedRole = roleRepository.save(role);
        return mapToDTO(updatedRole);
    }

    @Override
    public RoleDTO removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.getPermissions().remove(permission);
        Role updatedRole = roleRepository.save(role);
        return mapToDTO(updatedRole);
    }

    private RoleDTO mapToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissionIds(role.getPermissions().stream()
                        .map(Permission::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
} 