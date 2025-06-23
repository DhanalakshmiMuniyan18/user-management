package com.userservice.management.rbac.service;

import com.userservice.management.rbac.dto.PermissionDTO;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing permissions.
 * @author Saravanamuthukumar S
 */
public interface PermissionService {
    PermissionDTO createPermission(PermissionDTO permissionDTO);
    PermissionDTO getPermission(Long id);
    List<PermissionDTO> getAllPermissions();
    PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);
    void deletePermission(Long id);
    Set<PermissionDTO> getPermissionsByIds(Set<Long> ids);
} 