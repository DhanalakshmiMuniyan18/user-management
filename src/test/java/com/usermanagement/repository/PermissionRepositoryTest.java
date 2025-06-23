package com.usermanagement.repository;

import com.usermanagement.model.entity.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Saravanamuthukumar S
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void shouldSavePermission() {
        // Given
        Permission permission = new Permission();
        permission.setName("CREATE_USER");
        permission.setDescription("Can create new users");

        // When
        Permission savedPermission = permissionRepository.save(permission);

        // Then
        assertThat(savedPermission.getId()).isNotNull();
        assertThat(savedPermission.getName()).isEqualTo("CREATE_USER");
        assertThat(savedPermission.getDescription()).isEqualTo("Can create new users");
    }

    @Test
    void shouldFindByName() {
        // Given
        Permission permission = new Permission();
        permission.setName("DELETE_USER");
        permission.setDescription("Can delete users");
        permissionRepository.save(permission);

        // When
        var foundPermission = permissionRepository.findByName("DELETE_USER");

        // Then
        assertThat(foundPermission).isPresent();
        assertThat(foundPermission.get().getName()).isEqualTo("DELETE_USER");
    }

    @Test
    void shouldCheckIfNameExists() {
        // Given
        Permission permission = new Permission();
        permission.setName("VIEW_REPORTS");
        permissionRepository.save(permission);

        // When & Then
        assertThat(permissionRepository.existsByName("VIEW_REPORTS")).isTrue();
        assertThat(permissionRepository.existsByName("NONEXISTENT_PERMISSION")).isFalse();
    }

    @Test
    void shouldFindByNameIn() {
        // Given
        Permission permission1 = new Permission();
        permission1.setName("CREATE_ROLE");
        permissionRepository.save(permission1);

        Permission permission2 = new Permission();
        permission2.setName("DELETE_ROLE");
        permissionRepository.save(permission2);

        // When
        Set<Permission> permissions = permissionRepository.findByNameIn(
            Set.of("CREATE_ROLE", "DELETE_ROLE")
        );

        // Then
        assertThat(permissions).hasSize(2);
        assertThat(permissions).extracting("name")
            .containsExactlyInAnyOrder("CREATE_ROLE", "DELETE_ROLE");
    }
} 