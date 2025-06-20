package com.usermanagement.service.impl;

import com.usermanagement.dto.PermissionDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.PermissionMapper;
import com.usermanagement.model.entity.Permission;
import com.usermanagement.repository.PermissionRepository;
import com.usermanagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Saravanamuthukumar S
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionDto createPermission(PermissionDto permissionDto) {
        if (permissionRepository.existsByName(permissionDto.getName())) {
            throw new IllegalArgumentException("Permission name already exists");
        }

        Permission permission = permissionMapper.toEntity(permissionDto);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toDto(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDto getPermissionById(Long id) {
        return permissionRepository.findById(id)
            .map(permissionMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDto getPermissionByName(String name) {
        return permissionRepository.findByName(name)
            .map(permissionMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDto> getAllPermissions(String search, Pageable pageable) {
        return permissionRepository.findBySearchCriteria(search, pageable)
            .map(permissionMapper::toDto);
    }

    @Override
    public PermissionDto updatePermission(Long id, PermissionDto permissionDto) {
        Permission existingPermission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        if (!existingPermission.getName().equals(permissionDto.getName()) && 
            permissionRepository.existsByName(permissionDto.getName())) {
            throw new IllegalArgumentException("Permission name already exists");
        }

        existingPermission.setName(permissionDto.getName());
        existingPermission.setDescription(permissionDto.getDescription());

        Permission updatedPermission = permissionRepository.save(existingPermission);
        return permissionMapper.toDto(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        
        if (!permission.getRoles().isEmpty()) {
            throw new IllegalStateException("Cannot delete permission as it is assigned to roles");
        }
        
        permissionRepository.delete(permission);
    }
} 