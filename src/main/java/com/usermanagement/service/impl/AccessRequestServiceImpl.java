package com.usermanagement.service.impl;

import com.usermanagement.dto.AccessRequestDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.AccessRequestMapper;
import com.usermanagement.model.entity.AccessRequest;
import com.usermanagement.model.entity.AccessRequest.Status;
import com.usermanagement.model.entity.Role;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.AccessRequestRepository;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.AccessRequestService;
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
public class AccessRequestServiceImpl implements AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccessRequestMapper accessRequestMapper;

    @Override
    public AccessRequestDto createRequest(AccessRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));
        Role role = roleRepository.findById(requestDto.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + requestDto.getRoleId()));

        AccessRequest accessRequest = accessRequestMapper.toEntity(requestDto);
        accessRequest.setUser(user);
        accessRequest.setRole(role);
        accessRequest.setStatus(Status.PENDING);

        AccessRequest saved = accessRequestRepository.save(accessRequest);
        return accessRequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccessRequestDto getRequestById(Long id) {
        return accessRequestRepository.findById(id)
            .map(accessRequestMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("AccessRequest not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccessRequestDto> getAllRequests(String search, String status, Pageable pageable) {
        Status reqStatus = status != null ? Status.valueOf(status.toUpperCase()) : null;
        return accessRequestRepository.findBySearchCriteria(search, reqStatus, pageable)
            .map(accessRequestMapper::toDto);
    }

    @Override
    public AccessRequestDto approveRequest(Long id, String responseMessage) {
        AccessRequest request = accessRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccessRequest not found with id: " + id));
        if (request.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }
        request.setStatus(Status.APPROVED);
        request.setResponseMessage(responseMessage);
        AccessRequest updated = accessRequestRepository.save(request);
        return accessRequestMapper.toDto(updated);
    }

    @Override
    public AccessRequestDto rejectRequest(Long id, String responseMessage) {
        AccessRequest request = accessRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccessRequest not found with id: " + id));
        if (request.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }
        request.setStatus(Status.REJECTED);
        request.setResponseMessage(responseMessage);
        AccessRequest updated = accessRequestRepository.save(request);
        return accessRequestMapper.toDto(updated);
    }

    @Override
    public void deleteRequest(Long id) {
        AccessRequest request = accessRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AccessRequest not found with id: " + id));
        accessRequestRepository.delete(request);
    }
} 