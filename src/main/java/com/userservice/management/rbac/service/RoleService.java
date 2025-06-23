package com.userservice.management.rbac.service;

import com.userservice.management.rbac.dto.RoleDTO;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing roles.
 * @author Saravanamuthukumar S
 */
public interface RoleService {
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO getRole(Long id);
    List<RoleDTO> getAllRoles();
    RoleDTO updateRole(Long id, RoleDTO roleDTO);
    void deleteRole(Long id);
    RoleDTO assignPermissionsToRole(Long roleId, Set<Long> permissionIds);
    RoleDTO removePermissionFromRole(Long roleId, Long permissionId);
} 