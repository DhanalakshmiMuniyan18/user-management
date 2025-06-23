package com.userservice.management.model;

import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import com.userservice.management.rbac.model.Role;

/**
 * Unit tests for {@link User}.
 * @author Saravanamuthukumar S
 */
class UserTest {

    @Test
    void testBuilderAndGettersSetters() {
        Set<Role> roles = new HashSet<>();
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("secret")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .status(User.UserStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .roles(roles)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        assertThat(user.getRoles()).isSameAs(roles);
    }

    @Test
    void testNoArgsAndAllArgsConstructors() {
        User user = new User();
        user.setId(2L);
        user.setUsername("anotheruser");
        user.setPassword("pass");
        user.setEmail("another@example.com");
        user.setFirstName("Another");
        user.setLastName("User");
        user.setStatus(User.UserStatus.LOCKED);
        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getUsername()).isEqualTo("anotheruser");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getEmail()).isEqualTo("another@example.com");
        assertThat(user.getFirstName()).isEqualTo("Another");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getStatus()).isEqualTo(User.UserStatus.LOCKED);
    }

    @Test
    void testUserStatusEnumValues() {
        assertThat(User.UserStatus.valueOf("ACTIVE")).isEqualTo(User.UserStatus.ACTIVE);
        assertThat(User.UserStatus.valueOf("INACTIVE")).isEqualTo(User.UserStatus.INACTIVE);
        assertThat(User.UserStatus.valueOf("LOCKED")).isEqualTo(User.UserStatus.LOCKED);
        assertThat(User.UserStatus.valueOf("DELETED")).isEqualTo(User.UserStatus.DELETED);
    }

    @Test
    void testRolesRelationship() {
        User user = new User();
        Role role = Role.builder().id(1L).name("ADMIN").build();
        user.getRoles().add(role);
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder().id(1L).username("user").build();
        User user2 = User.builder().id(1L).username("user").build();
        assertThat(user1).isEqualTo(user1);
        assertThat(user1).isNotEqualTo(user2);
    }
} 