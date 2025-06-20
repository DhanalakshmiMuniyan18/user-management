package com.usermanagement.service.impl;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.RoleMapper;
import com.usermanagement.model.entity.Permission;
import com.usermanagement.model.entity.Role;
import com.usermanagement.repository.PermissionRepository;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Saravanamuthukumar S
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        if (roleRepository.existsByName(roleDto.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Role role = roleMapper.toEntity(roleDto);
        if (roleDto.getPermissionNames() != null && !roleDto.getPermissionNames().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByNameIn(roleDto.getPermissionNames());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(roleMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleByName(String name) {
        return roleRepository.findByName(name)
            .map(roleMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDto> getAllRoles(String search, Pageable pageable) {
        return roleRepository.findBySearchCriteria(search, pageable)
            .map(roleMapper::toDto);
    }

    @Override
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        Role existingRole = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!existingRole.getName().equals(roleDto.getName()) && 
            roleRepository.existsByName(roleDto.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        if (roleDto.getPermissionNames() != null) {
            Set<Permission> permissions = permissionRepository.findByNameIn(roleDto.getPermissionNames());
            existingRole.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete role as it is assigned to users");
        }
        
        roleRepository.delete(role);
    }

    @Override
    public RoleDto addPermissionsToRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Set<Permission> permissions = permissionRepository.findByNameIn(permissionNames);
        if (permissions.size() != permissionNames.size()) {
            Set<String> foundPermissions = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
            Set<String> notFound = permissionNames.stream()
                .filter(name -> !foundPermissions.contains(name))
                .collect(Collectors.toSet());
            throw new ResourceNotFoundException("Permissions not found: " + notFound);
        }

        role.getPermissions().addAll(permissions);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public RoleDto removePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Set<Permission> permissions = permissionRepository.findByNameIn(permissionNames);
        role.getPermissions().removeAll(permissions);
        
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }
} 