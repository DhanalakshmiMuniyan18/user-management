package com.usermanagement.repository;

import com.usermanagement.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Saravanamuthukumar S
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    boolean existsByName(String name);
    
    Set<Permission> findByNameIn(Set<String> names);

    @Query("SELECT p FROM Permission p WHERE (:search IS NULL OR p.name LIKE %:search% OR p.description LIKE %:search%)")
    org.springframework.data.domain.Page<Permission> findBySearchCriteria(@Param("search") String search, org.springframework.data.domain.Pageable pageable);
} 