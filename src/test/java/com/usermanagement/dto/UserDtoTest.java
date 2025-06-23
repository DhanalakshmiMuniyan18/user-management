package com.usermanagement.dto;

import com.usermanagement.model.entity.User.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setStatus(UserStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        user.setLastLogin(now);
        user.setCreatedAt(now.minusDays(1));
        user.setUpdatedAt(now);
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        user.setRoleNames(roles);

        assertEquals(1L, user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals(now, user.getLastLogin());
        assertEquals(now.minusDays(1), user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals(roles, user.getRoleNames());
    }

    @Test
    void equalsAndHashCode_ShouldDependOnIdOnly() {
        UserDto user1 = new UserDto();
        user1.setId(10L);
        user1.setName("A");
        UserDto user2 = new UserDto();
        user2.setId(10L);
        user2.setName("B");
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void notEquals_WhenIdIsDifferent() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        UserDto user2 = new UserDto();
        user2.setId(2L);
        assertNotEquals(user1, user2);
    }

    @Test
    void nullAndEdgeCases() {
        UserDto user = new UserDto();
        assertNotEquals(user, null);
        assertNotEquals(user, new Object());
        assertEquals(user, user); // self-equality
    }

    @Test
    void canHandleNullFields() {
        UserDto user = new UserDto();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setStatus(null);
        user.setLastLogin(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
        user.setRoleNames(null);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getStatus());
        assertNull(user.getLastLogin());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        assertNull(user.getRoleNames());
    }
} 