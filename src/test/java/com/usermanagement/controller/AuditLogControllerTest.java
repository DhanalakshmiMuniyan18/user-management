package com.usermanagement.controller;

import com.usermanagement.dto.AuditLogDto;
import com.usermanagement.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    private AuditLogDto auditLogDto;

    @BeforeEach
    void setUp() {
        auditLogDto = new AuditLogDto();
        auditLogDto.setId(1L);
        auditLogDto.setAction("CREATE_USER");
    }

    @Test
    void getAllLogs_Success() {
        Page<AuditLogDto> page = new PageImpl<>(List.of(auditLogDto));
        when(auditLogService.getAllLogs(any(), any())).thenReturn(page);
        var response = auditLogController.getAllLogs(null, PageRequest.of(0, 10));
        assertThat(response.getBody().getContent()).containsExactly(auditLogDto);
    }
} 