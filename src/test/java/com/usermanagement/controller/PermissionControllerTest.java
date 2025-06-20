package com.usermanagement.controller;

import com.usermanagement.dto.PermissionDto;
import com.usermanagement.service.PermissionService;
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
class PermissionControllerTest {
    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private PermissionDto permissionDto;

    @BeforeEach
    void setUp() {
        permissionDto = new PermissionDto();
        permissionDto.setId(1L);
        permissionDto.setName("READ_USER");
    }

    @Test
    void getAllPermissions_Success() {
        Page<PermissionDto> page = new PageImpl<>(List.of(permissionDto));
        when(permissionService.getAllPermissions(any(), any())).thenReturn(page);
        var response = permissionController.getAllPermissions(null, PageRequest.of(0, 10));
        assertThat(response.getBody().getContent()).containsExactly(permissionDto);
    }
} 