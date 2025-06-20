package com.usermanagement.service;

import com.usermanagement.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * @author Saravanamuthukumar S
 */
public interface RoleService {
    
    RoleDto createRole(RoleDto roleDto);
    
    RoleDto getRoleById(Long id);
    
    RoleDto getRoleByName(String name);
    
    Page<RoleDto> getAllRoles(String search, Pageable pageable);
    
    RoleDto updateRole(Long id, RoleDto roleDto);
    
    void deleteRole(Long id);
    
    RoleDto addPermissionsToRole(Long roleId, Set<String> permissionNames);
    
    RoleDto removePermissionsFromRole(Long roleId, Set<String> permissionNames);
} 