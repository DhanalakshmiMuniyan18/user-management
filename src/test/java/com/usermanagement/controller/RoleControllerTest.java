package com.usermanagement.controller;

import com.usermanagement.dto.RoleDto;
import com.usermanagement.service.RoleService;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto(1L, "ADMIN", null, Set.of());
    }

    @Test
    void getAllRoles_Success() {
        Page<RoleDto> page = new PageImpl<>(List.of(roleDto));
        when(roleService.getAllRoles(any(), any())).thenReturn(page);
        var response = roleController.getAllRoles(null, PageRequest.of(0, 10));
        assertThat(response.getBody().data().getContent()).containsExactly(roleDto);
    }
} 