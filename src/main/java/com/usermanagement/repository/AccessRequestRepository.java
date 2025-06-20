package com.usermanagement.repository;

import com.usermanagement.model.entity.AccessRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Saravanamuthukumar S
 */
@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    
    Page<AccessRequest> findByUserId(Long userId, Pageable pageable);
    
    Page<AccessRequest> findByStatus(AccessRequest.Status status, Pageable pageable);
    
    List<AccessRequest> findByUserIdAndStatus(Long userId, AccessRequest.Status status);
    
    @Query("SELECT ar FROM AccessRequest ar WHERE " +
           "(:userId IS NULL OR ar.user.id = :userId) AND " +
           "(:status IS NULL OR ar.status = :status)")
    Page<AccessRequest> findBySearchCriteria(
        @Param("userId") Long userId,
        @Param("status") AccessRequest.Status status,
        Pageable pageable
    );
    
    boolean existsByUserIdAndRoleIdAndStatus(Long userId, Long roleId, AccessRequest.Status status);

    @Query("SELECT ar FROM AccessRequest ar WHERE (:search IS NULL OR ar.user.email LIKE %:search% OR ar.role.name LIKE %:search%) AND (:status IS NULL OR ar.status = :status)")
    Page<AccessRequest> findBySearchCriteria(@Param("search") String search, @Param("status") AccessRequest.Status status, Pageable pageable);

    List<AccessRequest> findByStatus(AccessRequest.Status status);
} 