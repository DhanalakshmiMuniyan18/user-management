package com.usermanagement.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleCreation() {
        Role role = Role.builder()
                .name("ROLE_TEST")
                .description("Test role description")
                .build();

        assertNotNull(role);
        assertEquals("ROLE_TEST", role.getName());
        assertEquals("Test role description", role.getDescription());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    void testAddAndRemovePermission() {
        Role role = Role.builder()
                .name("ROLE_TEST")
                .description("Test role")
                .build();

        Permission permission = Permission.builder()
                .name("TEST_PERMISSION")
                .resource("TEST")
                .action("READ")
                .description("Test permission")
                .build();

        role.addPermission(permission);
        assertTrue(role.getPermissions().contains(permission));
        assertEquals(1, role.getPermissions().size());

        role.removePermission(permission);
        assertFalse(role.getPermissions().contains(permission));
        assertEquals(0, role.getPermissions().size());
    }

    @Test
    void testRoleValidation() {
        Role role = new Role();
        
        // Test name validation
        role.setName("ab"); // Too short
        assertThrows(IllegalArgumentException.class, () -> {
            role.validate();
        });

        role.setName("validrolename");
        
        // Test description validation
        role.setDescription("a".repeat(256)); // Too long
        assertThrows(IllegalArgumentException.class, () -> {
            role.validate();
        });

        role.setDescription("Valid description");
        
        // All validations should pass now
        assertDoesNotThrow(() -> {
            role.validate();
        });
    }
} 