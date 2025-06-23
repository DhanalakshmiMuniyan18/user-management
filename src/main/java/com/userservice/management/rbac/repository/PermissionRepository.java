package com.userservice.management.rbac.repository;

import com.userservice.management.rbac.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Permission entity.
 * @author Saravanamuthukumar S
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    Set<Permission> findByIdIn(Set<Long> ids);
} 