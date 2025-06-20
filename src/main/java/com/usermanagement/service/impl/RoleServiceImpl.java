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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Saravanamuthukumar S
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        log.info("Creating role: {}", roleDto.name());
        if (roleRepository.existsByName(roleDto.name())) {
            log.warn("Role name already exists: {}", roleDto.name());
            throw new IllegalArgumentException("Role name already exists");
        }
        Role role = roleMapper.toEntity(roleDto);
        if (roleDto.permissions() != null && !roleDto.permissions().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByNameIn(roleDto.permissions());
            role.setPermissions(permissions);
        }
        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole.getId());
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#id")
    public RoleDto getRoleById(Long id) {
        log.info("Fetching role by id: {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#name")
    public RoleDto getRoleByName(String name) {
        log.info("Fetching role by name: {}", name);
        return roleRepository.findByName(name)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RoleDto> getAllRoles(String search, Pageable pageable) {
        log.info("Listing roles with search: {}", search);
        return roleRepository.findBySearchCriteria(search, pageable)
                .map(roleMapper::toDto);
    }

    @Override
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        log.info("Updating role: {}", id);
        Role existingRole = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        if (!existingRole.getName().equals(roleDto.name()) && roleRepository.existsByName(roleDto.name())) {
            log.warn("Role name already exists: {}", roleDto.name());
            throw new IllegalArgumentException("Role name already exists");
        }
        existingRole.setName(roleDto.name());
        existingRole.setDescription(roleDto.description());
        if (roleDto.permissions() != null) {
            Set<Permission> permissions = permissionRepository.findByNameIn(roleDto.permissions());
            existingRole.setPermissions(permissions);
        }
        Role updatedRole = roleRepository.save(existingRole);
        log.info("Role updated: {}", updatedRole.getId());
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role: {}", id);
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        if (!role.getUsers().isEmpty()) {
            log.warn("Cannot delete role {} as it is assigned to users", id);
            throw new IllegalStateException("Cannot delete role as it is assigned to users");
        }
        roleRepository.delete(role);
        log.info("Role deleted: {}", id);
    }

    @Override
    public RoleDto addPermissionsToRole(Long roleId, Set<String> permissionNames) {
        log.info("Adding permissions to role: {}", roleId);
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        Set<Permission> permissionEntities = permissionRepository.findByNameIn(permissionNames);
        if (permissionEntities.size() != permissionNames.size()) {
            Set<String> foundPermissions = permissionEntities.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
            Set<String> notFound = permissionNames.stream()
                .filter(name -> !foundPermissions.contains(name))
                .collect(Collectors.toSet());
            log.warn("Permissions not found: {}", notFound);
            throw new ResourceNotFoundException("Permissions not found: " + notFound);
        }
        role.getPermissions().addAll(permissionEntities);
        Role updatedRole = roleRepository.save(role);
        log.info("Permissions added to role: {}", roleId);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public RoleDto removePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        log.info("Removing permissions from role: {}", roleId);
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        Set<Permission> permissionEntities = permissionRepository.findByNameIn(permissionNames);
        role.getPermissions().removeAll(permissionEntities);
        Role updatedRole = roleRepository.save(role);
        log.info("Permissions removed from role: {}", roleId);
        return roleMapper.toDto(updatedRole);
    }
} 