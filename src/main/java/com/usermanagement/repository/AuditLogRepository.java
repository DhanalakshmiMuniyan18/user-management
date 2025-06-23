package com.usermanagement.repository;

import com.usermanagement.model.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author Saravanamuthukumar S
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    Page<AuditLog> findByUserIdAndActionAndTimestampBetween(
        Long userId,
        String action,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );

    Page<AuditLog> findByUserIdAndActionAndTimestampGreaterThanEqualAndTimestampLessThanEqual(
        Long userId,
        String action,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );
} 