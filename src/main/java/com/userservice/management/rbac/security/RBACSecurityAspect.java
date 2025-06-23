package com.userservice.management.rbac.security;

import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.model.Role;
import com.userservice.management.rbac.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aspect for handling RBAC security checks.
 * This implementation handles various edge cases:
 * - Invalid role ID formats
 * - Empty permission arrays
 * - Roles without permissions
 * - Non-existent roles
 * - Multiple roles with conflicting permissions
 * - Case-sensitive permission names
 * - Special characters in permission names
 * - Anonymous authentication
 * - Null authentication
 * 
 * @author Saravanamuthukumar S
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RBACSecurityAspect {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final int MAX_ROLES_PER_USER = 100; // Configurable maximum number of roles per user
    
    private final RoleRepository roleRepository;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // Validate input parameters
        if (requirePermission == null || requirePermission.value() == null) {
            throw new IllegalArgumentException("RequirePermission annotation cannot be null and must have value");
        }

        // Get and validate authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(authentication);

        String username = authentication.getName();
        if (ANONYMOUS_USER.equals(username)) {
            throw new SecurityException("Anonymous users are not allowed to access secured methods");
        }

        // Extract and validate role IDs
        Set<Long> roleIds = extractRoleIds(authentication);
        validateRoleIds(roleIds, username);

        // Fetch and validate roles
        Set<Role> userRoles = fetchAndValidateRoles(roleIds, username);

        // Extract and validate permissions
        Set<String> userPermissions = extractPermissions(userRoles);
        log.debug("User permissions for {}: {}", username, userPermissions);

        // Validate required permissions
        String[] requiredPermissions = requirePermission.value();
        boolean allRequired = requirePermission.allRequired();
        validateRequiredPermissions(requiredPermissions, userPermissions, username, allRequired);

        // Execute the method
        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error executing secured method for user {}: {}", username, t.getMessage());
            throw t;
        }
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
    }

    private Set<Long> extractRoleIds(Authentication authentication) {
        Set<Long> roleIds = authentication.getAuthorities().stream()
                .map(authority -> {
                    String roleId = authority.getAuthority();
                    if (!roleId.startsWith(ROLE_PREFIX)) {
                        log.warn("Invalid role format: {}", roleId);
                        return null;
                    }
                    try {
                        return Long.parseLong(roleId.substring(ROLE_PREFIX.length()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid role ID format: {}", roleId);
                        return null;
                    }
                })
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (roleIds.isEmpty()) {
            throw new SecurityException("No valid roles found");
        }

        return roleIds;
    }

    private void validateRoleIds(Set<Long> roleIds, String username) {
        if (roleIds.size() > MAX_ROLES_PER_USER) {
            throw new SecurityException("User " + username + " exceeds maximum allowed roles: " + MAX_ROLES_PER_USER);
        }
    }

    private Set<Role> fetchAndValidateRoles(Set<Long> roleIds, String username) {
        Set<Role> userRoles = roleRepository.findByIdInWithPermissions(roleIds);
        
        if (userRoles.isEmpty()) {
            throw new SecurityException("No roles found for user " + username);
        }

        // Validate each role has permissions
        userRoles.forEach(role -> {
            if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
                log.warn("Role {} has no permissions", role.getName());
            }
        });

        return userRoles;
    }

    private Set<String> extractPermissions(Set<Role> userRoles) {
        return userRoles.stream()
                .filter(role -> role.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> StringUtils.hasText(permission.getName()))
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    private void validateRequiredPermissions(String[] requiredPermissions, Set<String> userPermissions, String username, boolean allRequired) {
        // Handle empty permissions array
        if (requiredPermissions.length == 0) {
            log.debug("No permissions required, access granted");
            return;
        }

        // Validate each required permission
        Arrays.stream(requiredPermissions)
                .forEach(permission -> {
                    if (!StringUtils.hasText(permission)) {
                        throw new IllegalArgumentException("Required permission cannot be null or empty");
                    }
                });

        boolean hasPermission;
        if (allRequired) {
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new SecurityException("User " + username + " does not have the required permissions: " +
                    String.join(", ", requiredPermissions));
        }
    }
} 