package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class RoleDtoTest {

    @Test
    void recordEqualityAndHashCode() {
        Set<String> perms = Set.of("READ", "WRITE");
        RoleDto r1 = new RoleDto(1L, "ADMIN", "desc", perms);
        RoleDto r2 = new RoleDto(1L, "ADMIN", "desc", perms);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void notEquals_WhenIdOrFieldsDiffer() {
        RoleDto r1 = new RoleDto(1L, "ADMIN", "desc", Set.of("READ"));
        RoleDto r2 = new RoleDto(2L, "ADMIN", "desc", Set.of("READ"));
        assertNotEquals(r1, r2);
        RoleDto r3 = new RoleDto(1L, "USER", "desc", Set.of("READ"));
        assertNotEquals(r1, r3);
    }

    @Test
    void canHandleNullFields() {
        RoleDto role = new RoleDto(null, null, null, null);
        assertNull(role.id());
        assertNull(role.name());
        assertNull(role.description());
        assertNull(role.permissions());
    }

    @Test
    void permissionsSetReflectsOriginalSetChanges() {
        Set<String> perms = new HashSet<>();
        perms.add("READ");
        RoleDto role = new RoleDto(1L, "ADMIN", "desc", perms);
        assertTrue(role.permissions().contains("READ"));
        perms.add("WRITE");
        // By default, record does not make a defensive copy, so the change is reflected
        assertTrue(role.permissions().contains("WRITE"));
        // Document: If you want immutability, make a defensive copy in the record constructor
    }

    @Test
    void toStringIsNotNull() {
        RoleDto role = new RoleDto(1L, "ADMIN", "desc", Set.of("READ"));
        assertNotNull(role.toString());
    }
} 