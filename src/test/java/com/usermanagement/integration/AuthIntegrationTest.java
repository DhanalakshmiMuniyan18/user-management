package com.usermanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.config.TestConfig;
import com.usermanagement.model.entity.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.model.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;

/**
 * @author Saravanamuthukumar S
 */
@AutoConfigureMockMvc
public class AuthIntegrationTest extends IntegrationTestConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void login_Success() throws Exception {
        Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                Role r = new Role();
                r.setName("USER");
                r.setDescription("Basic user role");
                return roleRepository.save(r);
            });
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRoles(new HashSet<>());
        user.getRoles().add(userRole);
        userRepository.saveAndFlush(user);

        // DEBUG: Print user from DB before login
        User dbUser = userRepository.findByEmail("test@test.com").orElse(null);
        System.out.println("[DEBUG] User in DB before login: " + dbUser);
        assert dbUser != null : "User should exist in DB before login";
        assert passwordEncoder.matches("password", dbUser.getPassword()) : "Password should match after encoding";
        System.out.println("[DEBUG] PasswordEncoder class in test: " + passwordEncoder.getClass());
        System.out.println("[DEBUG] Password hash in DB: " + dbUser.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\", \"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void login_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\", \"password\":\"wrong\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
} 