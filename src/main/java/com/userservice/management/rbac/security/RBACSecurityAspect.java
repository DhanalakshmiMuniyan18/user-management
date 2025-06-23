package com.userservice.management.rbac.security;

import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.model.Role;
import com.userservice.management.rbac.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aspect for handling RBAC security checks.
 * @author Saravanamuthukumar S
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RBACSecurityAspect {

    private final RoleRepository roleRepository;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        String username = authentication.getName();
        Set<Role> userRoles = roleRepository.findByIdInWithPermissions(
                authentication.getAuthorities().stream()
                        .map(authority -> Long.parseLong(authority.getAuthority().replace("ROLE_", "")))
                        .collect(Collectors.toSet())
        );

        Set<String> userPermissions = userRoles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        String[] requiredPermissions = requirePermission.value();
        boolean hasPermission;

        if (requirePermission.allRequired()) {
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

        return joinPoint.proceed();
    }
} 