package com.usermanagement.service;

import com.usermanagement.dto.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Saravanamuthukumar S
 */
public interface AuditLogService {
    AuditLogDto createLog(AuditLogDto auditLogDto);
    AuditLogDto getLogById(Long id);
    Page<AuditLogDto> getAllLogs(String search, Pageable pageable);
    void deleteLog(Long id);
} 