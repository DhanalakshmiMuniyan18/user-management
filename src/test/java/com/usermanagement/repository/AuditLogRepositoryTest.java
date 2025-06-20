package com.usermanagement.repository;

import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Saravanamuthukumar S
 */
@DataJpaTest
@ActiveProfiles("test")
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);
        
        now = LocalDateTime.now();
    }

    @Test
    void shouldSaveAuditLog() {
        // Given
        AuditLog log = AuditLog.of(testUser, "USER_LOGIN", "User logged in successfully");

        // When
        AuditLog savedLog = auditLogRepository.save(log);

        // Then
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getUser()).isEqualTo(testUser);
        assertThat(savedLog.getAction()).isEqualTo("USER_LOGIN");
        assertThat(savedLog.getDetails()).isEqualTo("User logged in successfully");
    }

    @Test
    void shouldFindByUserId() {
        // Given
        AuditLog log1 = AuditLog.of(testUser, "ACTION_1", "Details 1");
        AuditLog log2 = AuditLog.of(testUser, "ACTION_2", "Details 2");
        auditLogRepository.save(log1);
        auditLogRepository.save(log2);

        // When
        Page<AuditLog> logs = auditLogRepository.findByUserId(testUser.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(logs.getContent()).hasSize(2);
        assertThat(logs.getContent()).extracting("action")
            .containsExactlyInAnyOrder("ACTION_1", "ACTION_2");
    }

    @Test
    void shouldFindByAction() {
        // Given
        AuditLog log1 = AuditLog.of(testUser, "LOGIN", "Login 1");
        AuditLog log2 = AuditLog.of(testUser, "LOGIN", "Login 2");
        AuditLog log3 = AuditLog.of(testUser, "LOGOUT", "Logout");
        auditLogRepository.saveAll(java.util.List.of(log1, log2, log3));

        // When
        Page<AuditLog> loginLogs = auditLogRepository.findByAction("LOGIN", PageRequest.of(0, 10));

        // Then
        assertThat(loginLogs.getContent()).hasSize(2);
        assertThat(loginLogs.getContent()).extracting("action")
            .containsOnly("LOGIN");
    }

    @Test
    void shouldFindBySearchCriteria() {
        // Given
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        AuditLog log1 = AuditLog.of(testUser, "LOGIN", "Past login");
        AuditLog log2 = AuditLog.of(testUser, "LOGOUT", "Recent logout");
        auditLogRepository.saveAll(java.util.List.of(log1, log2));

        // When
        Page<AuditLog> filteredLogs = auditLogRepository.findBySearchCriteria(
            testUser.getId(),
            "LOGIN",
            yesterday,
            tomorrow,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(filteredLogs.getContent()).hasSize(1);
        assertThat(filteredLogs.getContent().get(0).getAction()).isEqualTo("LOGIN");
    }
} 