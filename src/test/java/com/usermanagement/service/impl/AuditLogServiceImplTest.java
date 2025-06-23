package com.usermanagement.service.impl;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.AuditLogMapper;
import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogMapper auditLogMapper;
    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog auditLog;
    private AuditLogDto auditLogDto;
    private User user;
    private final Long logId = 1L;
    private final Long userId = 2L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        auditLog = new AuditLog();
        auditLog.setId(logId);
        auditLog.setUser(user);

        auditLogDto = new AuditLogDto();
        auditLogDto.setId(logId);
        auditLogDto.setUserId(userId);
    }

    @Test
    void createLog_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(auditLogMapper.toEntity(auditLogDto)).thenReturn(auditLog);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        when(auditLogMapper.toDto(auditLog)).thenReturn(auditLogDto);
        AuditLogDto result = auditLogService.createLog(auditLogDto);
        assertNotNull(result);
        assertEquals(logId, result.getId());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void createLog_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.createLog(auditLogDto));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void getLogById_Success() {
        when(auditLogRepository.findById(logId)).thenReturn(Optional.of(auditLog));
        when(auditLogMapper.toDto(auditLog)).thenReturn(auditLogDto);
        AuditLogDto result = auditLogService.getLogById(logId);
        assertNotNull(result);
        assertEquals(logId, result.getId());
    }

    @Test
    void getLogById_NotFound_ThrowsException() {
        when(auditLogRepository.findById(logId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.getLogById(logId));
    }

    @Test
    void getAllLogs_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> logPage = new PageImpl<>(Collections.singletonList(auditLog));
        when(auditLogRepository.findAll(pageable)).thenReturn(logPage);
        when(auditLogMapper.toDto(auditLog)).thenReturn(auditLogDto);
        Page<AuditLogDto> result = auditLogService.getAllLogs("search", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteLog_Success() {
        when(auditLogRepository.findById(logId)).thenReturn(Optional.of(auditLog));
        assertDoesNotThrow(() -> auditLogService.deleteLog(logId));
        verify(auditLogRepository).delete(auditLog);
    }

    @Test
    void deleteLog_NotFound_ThrowsException() {
        when(auditLogRepository.findById(logId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> auditLogService.deleteLog(logId));
        verify(auditLogRepository, never()).delete(any(AuditLog.class));
    }
} 