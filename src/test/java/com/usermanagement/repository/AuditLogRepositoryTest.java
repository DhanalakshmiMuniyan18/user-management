package com.usermanagement.repository;

import com.usermanagement.model.entity.AuditLog;
import com.usermanagement.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        
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
        AuditLog log = new AuditLog();
        log.setUser(testUser);
        log.setAction("USER_LOGIN");
        log.setDetails("User logged in successfully");
        log.setTimestamp(now);

        // When
        AuditLog savedLog = auditLogRepository.save(log);

        // Then
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getUser()).isEqualTo(testUser);
        assertThat(savedLog.getAction()).isEqualTo("USER_LOGIN");
        assertThat(savedLog.getDetails()).isEqualTo("User logged in successfully");
        assertThat(savedLog.getTimestamp()).isNotNull();
    }

    @Test
    void shouldFindByUserId() {
        // Given
        AuditLog log1 = new AuditLog();
        log1.setUser(testUser);
        log1.setAction("ACTION_1");
        log1.setDetails("Details 1");
        log1.setTimestamp(now);

        AuditLog log2 = new AuditLog();
        log2.setUser(testUser);
        log2.setAction("ACTION_2");
        log2.setDetails("Details 2");
        log2.setTimestamp(now.plusHours(1));

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
        AuditLog log1 = new AuditLog();
        log1.setUser(testUser);
        log1.setAction("LOGIN");
        log1.setDetails("Login 1");
        log1.setTimestamp(now);

        AuditLog log2 = new AuditLog();
        log2.setUser(testUser);
        log2.setAction("LOGIN");
        log2.setDetails("Login 2");
        log2.setTimestamp(now.plusMinutes(30));

        AuditLog log3 = new AuditLog();
        log3.setUser(testUser);
        log3.setAction("LOGOUT");
        log3.setDetails("Logout");
        log3.setTimestamp(now.plusHours(1));

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

        AuditLog log1 = new AuditLog();
        log1.setUser(testUser);
        log1.setAction("LOGIN");
        log1.setDetails("Past login");
        log1.setTimestamp(now);

        AuditLog log2 = new AuditLog();
        log2.setUser(testUser);
        log2.setAction("LOGOUT");
        log2.setDetails("Recent logout");
        log2.setTimestamp(now.plusHours(1));

        auditLogRepository.saveAll(java.util.List.of(log1, log2));

        // When
        Page<AuditLog> filteredLogs = auditLogRepository.findByUserIdAndActionAndTimestampBetween(
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