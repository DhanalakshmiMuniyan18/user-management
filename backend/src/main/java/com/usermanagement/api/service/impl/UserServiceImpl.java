package com.usermanagement.api.service.impl;

import com.usermanagement.api.dto.UserDTO;
import com.usermanagement.api.dto.UserDeactivationRequest;
import com.usermanagement.api.dto.UserUpdateDTO;
import com.usermanagement.api.exception.EmailAlreadyExistsException;
import com.usermanagement.api.exception.ResourceNotFoundException;
import com.usermanagement.api.model.AuditLog;
import com.usermanagement.api.model.User;
import com.usermanagement.api.model.UserStatus;
import com.usermanagement.api.repository.AuditLogRepository;
import com.usermanagement.api.repository.UserRepository;
import com.usermanagement.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (!user.getEmail().equals(userUpdateDTO.getEmail()) && 
            userRepository.existsByEmail(userUpdateDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setEmail(userUpdateDTO.getEmail());
        user.setPhoneNumber(userUpdateDTO.getPhoneNumber());

        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO addRoleToUser(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.addRole(roleName);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO removeRoleFromUser(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.removeRole(roleName);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return userRepository.findById(userId)
                .map(user -> user.getUsername().equals(authentication.getName()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void deactivateUser(Long targetUserId, UserDeactivationRequest request, Long adminUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalStateException("User is already deactivated");
        }
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        AuditLog log = AuditLog.builder()
                .adminUserId(adminUserId)
                .targetUserId(targetUserId)
                .reason(request.getReason())
                .actionType("DEACTIVATE_USER")
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public Long getAdminUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated admin user found");
        }
        String username = authentication.getName();
        // Assumes username is unique
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Admin user not found"));
        return user.getId();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles().stream().collect(Collectors.toList()))
                .build();
    }
}
