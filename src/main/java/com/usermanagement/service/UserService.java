package com.usermanagement.service;

import com.usermanagement.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Saravanamuthukumar S
 */
public interface UserService {
    
    UserDto createUser(UserDto userDto);
    
    UserDto getUserById(Long id);
    
    Page<UserDto> getAllUsers(String search, String status, Pageable pageable);
    
    UserDto updateUser(Long id, UserDto userDto);
    
    void deleteUser(Long id);
    
    boolean existsByEmail(String email);
} 