package com.usermanagement.service;

import com.usermanagement.dto.AccessRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Saravanamuthukumar S
 */
public interface AccessRequestService {
    AccessRequestDto createRequest(AccessRequestDto requestDto);
    AccessRequestDto getRequestById(Long id);
    Page<AccessRequestDto> getAllRequests(String search, String status, Pageable pageable);
    AccessRequestDto approveRequest(Long id, String responseMessage);
    AccessRequestDto rejectRequest(Long id, String responseMessage);
    void deleteRequest(Long id);
} 