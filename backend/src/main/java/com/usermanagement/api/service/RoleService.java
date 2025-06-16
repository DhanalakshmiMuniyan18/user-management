package com.usermanagement.api.service;

import com.usermanagement.api.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    RoleDTO getRoleById(Long id);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(Long id, RoleDTO roleDTO);
    void deleteRole(Long id);
    RoleDTO addPermissionToRole(Long id, String permissionName);
    RoleDTO removePermissionFromRole(Long id, String permissionName);
} 