package com.usermanagement.controller;

import com.usermanagement.dto.UserDto;
import com.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.usermanagement.dto.StandardResponse;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<StandardResponse<UserDto>> createUser(@RequestBody @Valid UserDto userDto) {
         try {
            UserDto createdUser = userService.createUser(userDto);
            return new ResponseEntity<>(StandardResponse.success(createdUser, null), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(StandardResponse.error(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
} 