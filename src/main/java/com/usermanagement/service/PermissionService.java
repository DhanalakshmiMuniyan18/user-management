package com.usermanagement.service;

import com.usermanagement.dto.PermissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Saravanamuthukumar S
 */
public interface PermissionService {
    
    PermissionDto createPermission(PermissionDto permissionDto);
    
    PermissionDto getPermissionById(Long id);
    
    PermissionDto getPermissionByName(String name);
    
    Page<PermissionDto> getAllPermissions(String search, Pageable pageable);
    
    PermissionDto updatePermission(Long id, PermissionDto permissionDto);
    
    void deletePermission(Long id);
} 