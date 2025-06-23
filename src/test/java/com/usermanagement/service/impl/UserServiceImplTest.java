package com.usermanagement.service.impl;

import com.usermanagement.dto.UserDto;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.UserMapper;
import com.usermanagement.model.entity.User;
import com.usermanagement.model.entity.User.UserStatus;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private final Long userId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setName("Test User");
        user.setPassword("encoded");
        user.setStatus(UserStatus.ACTIVE);

        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setEmail(email);
        userDto.setName("Test User");
        userDto.setPassword("plain");
        userDto.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(email)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.getUserById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findBySearchCriteria(any(), any(), eq(pageable))).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userDto);
        Page<UserDto> result = userService.getAllUsers("search", "ACTIVE", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        userDto.setName("Updated Name");
        UserDto result = userService.updateUser(userId, userDto);
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void existsByEmail_ReturnsCorrectValue() {
        when(userRepository.existsByEmail(email)).thenReturn(true);
        assertTrue(userService.existsByEmail(email));
        when(userRepository.existsByEmail(email)).thenReturn(false);
        assertFalse(userService.existsByEmail(email));
    }
} 