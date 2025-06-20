package com.usermanagement.controller;

import com.usermanagement.dto.AccessRequestDto;
import com.usermanagement.service.AccessRequestService;
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

import com.usermanagement.model.entity.AccessRequest;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class AccessRequestControllerTest {
    @Mock
    private AccessRequestService accessRequestService;

    @InjectMocks
    private AccessRequestController accessRequestController;

    private AccessRequestDto accessRequestDto;

    @BeforeEach
    void setUp() {
        accessRequestDto = new AccessRequestDto();
        accessRequestDto.setId(1L);
        accessRequestDto.setStatus(AccessRequest.Status.PENDING);
    }

    @Test
    void getAllRequests_Success() {
        Page<AccessRequestDto> page = new PageImpl<>(List.of(accessRequestDto));
        when(accessRequestService.getAllRequests(any(), any(), any())).thenReturn(page);
        var response = accessRequestController.getAllRequests(null, null, PageRequest.of(0, 10));
        assertThat(response.getBody().getContent()).containsExactly(accessRequestDto);
    }
} 