package com.usermanagement.api.service.impl;

import com.usermanagement.api.dto.RoleDTO;
import com.usermanagement.api.exception.ResourceNotFoundException;
import com.usermanagement.api.model.Permission;
import com.usermanagement.api.model.Role;
import com.usermanagement.api.repository.PermissionRepository;
import com.usermanagement.api.repository.RoleRepository;
import com.usermanagement.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        return convertToDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        return convertToDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoleDTO addPermissionToRole(Long id, String permissionName) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        Permission permission = permissionRepository.findByName(permissionName);
        if (permission == null) {
            throw new ResourceNotFoundException("Permission", "name", permissionName);
        }

        role.getPermissions().add(permission);
        return convertToDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleDTO removePermissionFromRole(Long id, String permissionName) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        Permission permission = permissionRepository.findByName(permissionName);
        if (permission == null) {
            throw new ResourceNotFoundException("Permission", "name", permissionName);
        }

        role.getPermissions().remove(permission);
        return convertToDTO(roleRepository.save(role));
    }

    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
} 