package com.usermanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.dto.UserDto;
import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import com.usermanagement.model.entity.User.UserStatus;
import com.usermanagement.repository.AuditLogRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.security.UserPrincipal;
import com.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
class AuditLoggingAspectIntegrationTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        adminUser = new User();
        adminUser.setName("Admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("adminpass"));
        adminUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(adminUser);
        
        UserPrincipal principal = UserPrincipal.fromUser(adminUser);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
        objectMapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
    }

    @Test
    void createUser_createsAuditLog() throws Exception {
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("testuser@example.com");
        newUserDto.setPassword("password");

        System.out.println("Serialized UserDto: " + objectMapper.writeValueAsString(newUserDto));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated());

        List<AuditLog> logs = auditLogRepository.findAll();
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getUser().getEmail()).isEqualTo("admin@example.com");
        assertThat(logs.get(0).getAction()).isEqualTo("createUser");
        assertThat(logs.get(0).getDetails()).contains("Created user with email: testuser@example.com");
    }
} 