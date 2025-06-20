package com.usermanagement.repository;

import com.usermanagement.model.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Saravanamuthukumar S
 */
@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveRole() {
        // Given
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator role");

        // When
        Role savedRole = roleRepository.save(role);

        // Then
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ROLE_ADMIN");
        assertThat(savedRole.getDescription()).isEqualTo("Administrator role");
    }

    @Test
    void shouldFindByName() {
        // Given
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setDescription("Basic user role");
        roleRepository.save(role);

        // When
        var foundRole = roleRepository.findByName("ROLE_USER");

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldCheckIfNameExists() {
        // Given
        Role role = new Role();
        role.setName("ROLE_MANAGER");
        roleRepository.save(role);

        // When & Then
        assertThat(roleRepository.existsByName("ROLE_MANAGER")).isTrue();
        assertThat(roleRepository.existsByName("ROLE_NONEXISTENT")).isFalse();
    }

    @Test
    void shouldFindByNameIn() {
        // Given
        Role role1 = new Role();
        role1.setName("ROLE_ADMIN");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("ROLE_USER");
        roleRepository.save(role2);

        // When
        Set<Role> roles = roleRepository.findByNameIn(Set.of("ROLE_ADMIN", "ROLE_USER"));

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting("name").containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
} 