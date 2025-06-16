package com.usermanagement.api.service;

import com.usermanagement.api.dto.UserDTO;
import com.usermanagement.api.dto.UserUpdateDTO;
import com.usermanagement.api.dto.UserDeactivationRequest;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void deleteUser(Long id);
    UserDTO addRoleToUser(Long id, String roleName);
    UserDTO removeRoleFromUser(Long id, String roleName);
    boolean isCurrentUser(Long userId);

    /**
     * Deactivate a user (soft delete) and log the action.
     * @param targetUserId The user to deactivate
     * @param request The deactivation reason
     * @param adminUserId The admin performing the action
     */
    void deactivateUser(Long targetUserId, UserDeactivationRequest request, Long adminUserId);

    /**
     * Returns the current authenticated admin's user ID.
     */
    Long getAdminUserId();
}
