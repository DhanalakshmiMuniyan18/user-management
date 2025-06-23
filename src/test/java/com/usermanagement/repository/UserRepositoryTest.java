package com.usermanagement.repository;

import com.usermanagement.model.entity.User;
import com.usermanagement.model.entity.User.UserStatus;
import com.usermanagement.model.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import com.usermanagement.config.TestConfig;
import com.usermanagement.config.AuditingConfig;

/**
 * @author Saravanamuthukumar S
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(AuditingConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);
    }

    @Test
    void shouldSaveUser() {
        // When
        var foundUser = userRepository.findById(testUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByEmail() {
        // When
        var foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    void shouldCheckIfEmailExists() {
        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void shouldFindByStatus() {
        // Given
        testUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(testUser);

        // When
        Page<User> activeUsers = userRepository.findByStatus(
            UserStatus.ACTIVE,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(activeUsers.getContent()).hasSize(1);
        assertThat(activeUsers.getContent().get(0).getStatus())
            .isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldFindBySearchCriteria() {
        // Given
        testUser.setName("Specific User");
        userRepository.save(testUser);

        // When
        Page<User> filteredUsers = userRepository.findBySearchCriteria(
            "Specific",
            null,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(filteredUsers.getContent()).hasSize(1);
        assertThat(filteredUsers.getContent().get(0).getName())
            .isEqualTo("Specific User");
    }
} 