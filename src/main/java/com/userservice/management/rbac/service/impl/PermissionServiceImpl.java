package com.userservice.management.rbac.service.impl;

import com.userservice.management.rbac.dto.PermissionDTO;
import com.userservice.management.rbac.exception.DuplicateResourceException;
import com.userservice.management.rbac.exception.ResourceNotFoundException;
import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.repository.PermissionRepository;
import com.userservice.management.rbac.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the PermissionService interface.
 * @author Saravanamuthukumar S
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            throw new DuplicateResourceException("Permission", "name", permissionDTO.getName());
        }

        Permission permission = Permission.builder()
                .name(permissionDTO.getName())
                .description(permissionDTO.getDescription())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return mapToDTO(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        return mapToDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));

        if (!permission.getName().equals(permissionDTO.getName()) &&
                permissionRepository.existsByName(permissionDTO.getName())) {
            throw new DuplicateResourceException("Permission", "name", permissionDTO.getName());
        }

        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());

        Permission updatedPermission = permissionRepository.save(permission);
        return mapToDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission", "id", id);
        }
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getPermissionsByIds(Set<Long> ids) {
        return permissionRepository.findByIdIn(ids).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }

    private PermissionDTO mapToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
} 