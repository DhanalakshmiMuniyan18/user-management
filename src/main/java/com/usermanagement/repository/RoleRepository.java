package com.usermanagement.repository;

import com.usermanagement.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

/**
 * @author Saravanamuthukumar S
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
    
    Set<Role> findByNameIn(Set<String> names);

    @Query("SELECT r FROM Role r WHERE (:search IS NULL OR r.name LIKE %:search% OR r.description LIKE %:search%)")
    Page<Role> findBySearchCriteria(@Param("search") String search, Pageable pageable);
} 