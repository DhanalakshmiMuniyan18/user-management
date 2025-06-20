package com.usermanagement.service.impl;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.AuditLogMapper;
import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Saravanamuthukumar S
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public AuditLogDto createLog(AuditLogDto auditLogDto) {
        User user = userRepository.findById(auditLogDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + auditLogDto.getUserId()));
        AuditLog auditLog = auditLogMapper.toEntity(auditLogDto);
        auditLog.setUser(user);
        AuditLog saved = auditLogRepository.save(auditLog);
        return auditLogMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDto getLogById(Long id) {
        return auditLogRepository.findById(id)
            .map(auditLogMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("AuditLog not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllLogs(String search, Pageable pageable) {
        return auditLogRepository.findBySearchCriteria(search, pageable)
            .map(auditLogMapper::toDto);
    }

    @Override
    public void deleteLog(Long id) {
        AuditLog log = auditLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AuditLog not found with id: " + id));
        auditLogRepository.delete(log);
    }
} 