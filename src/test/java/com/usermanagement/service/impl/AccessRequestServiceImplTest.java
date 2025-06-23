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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceImplTest {

    @Mock
    private AccessRequestRepository accessRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AccessRequestMapper accessRequestMapper;
    @InjectMocks
    private AccessRequestServiceImpl accessRequestService;

    private AccessRequest accessRequest;
    private AccessRequestDto accessRequestDto;
    private User user;
    private Role role;
    private final Long requestId = 1L;
    private final Long userId = 2L;
    private final Long roleId = 3L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        role = new Role();
        role.setId(roleId);
        accessRequest = new AccessRequest();
        accessRequest.setId(requestId);
        accessRequest.setUser(user);
        accessRequest.setRole(role);
        accessRequest.setStatus(Status.PENDING);
        accessRequestDto = new AccessRequestDto();
        accessRequestDto.setId(requestId);
        accessRequestDto.setUserId(userId);
        accessRequestDto.setRoleId(roleId);
        accessRequestDto.setStatus(Status.PENDING);
    }

    @Test
    void createRequest_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(accessRequestMapper.toEntity(accessRequestDto)).thenReturn(accessRequest);
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(accessRequest);
        when(accessRequestMapper.toDto(accessRequest)).thenReturn(accessRequestDto);
        AccessRequestDto result = accessRequestService.createRequest(accessRequestDto);
        assertNotNull(result);
        assertEquals(requestId, result.getId());
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.createRequest(accessRequestDto));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void createRequest_RoleNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.createRequest(accessRequestDto));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void getRequestById_Success() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        when(accessRequestMapper.toDto(accessRequest)).thenReturn(accessRequestDto);
        AccessRequestDto result = accessRequestService.getRequestById(requestId);
        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void getRequestById_NotFound_ThrowsException() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.getRequestById(requestId));
    }

    @Test
    void getAllRequests_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AccessRequest> requestPage = new PageImpl<>(Collections.singletonList(accessRequest));
        when(accessRequestRepository.findBySearchCriteria(anyString(), any(), eq(pageable))).thenReturn(requestPage);
        when(accessRequestMapper.toDto(accessRequest)).thenReturn(accessRequestDto);
        Page<AccessRequestDto> result = accessRequestService.getAllRequests("search", "PENDING", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void approveRequest_Success() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(accessRequest);
        when(accessRequestMapper.toDto(accessRequest)).thenReturn(accessRequestDto);
        AccessRequestDto result = accessRequestService.approveRequest(requestId, "Approved");
        assertNotNull(result);
        assertEquals(Status.APPROVED, accessRequest.getStatus());
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    @Test
    void approveRequest_NotPending_ThrowsException() {
        accessRequest.setStatus(Status.REJECTED);
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        assertThrows(IllegalStateException.class, () -> accessRequestService.approveRequest(requestId, "Approved"));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void approveRequest_NotFound_ThrowsException() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.approveRequest(requestId, "Approved"));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void rejectRequest_Success() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(accessRequest);
        when(accessRequestMapper.toDto(accessRequest)).thenReturn(accessRequestDto);
        AccessRequestDto result = accessRequestService.rejectRequest(requestId, "Rejected");
        assertNotNull(result);
        assertEquals(Status.REJECTED, accessRequest.getStatus());
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    @Test
    void rejectRequest_NotPending_ThrowsException() {
        accessRequest.setStatus(Status.APPROVED);
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        assertThrows(IllegalStateException.class, () -> accessRequestService.rejectRequest(requestId, "Rejected"));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void rejectRequest_NotFound_ThrowsException() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.rejectRequest(requestId, "Rejected"));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void deleteRequest_Success() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(accessRequest));
        assertDoesNotThrow(() -> accessRequestService.deleteRequest(requestId));
        verify(accessRequestRepository).delete(accessRequest);
    }

    @Test
    void deleteRequest_NotFound_ThrowsException() {
        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accessRequestService.deleteRequest(requestId));
        verify(accessRequestRepository, never()).delete(any(AccessRequest.class));
    }
} 