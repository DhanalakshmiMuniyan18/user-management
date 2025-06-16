package com.usermanagement.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void testPermissionCreation() {
        Permission permission = Permission.builder()
                .name("TEST_PERMISSION")
                .resource("TEST")
                .action("READ")
                .description("Test permission description")
                .build();

        assertNotNull(permission);
        assertEquals("TEST_PERMISSION", permission.getName());
        assertEquals("TEST", permission.getResource());
        assertEquals("READ", permission.getAction());
        assertEquals("Test permission description", permission.getDescription());
    }

    @Test
    void testPermissionValidation() {
        Permission permission = new Permission();
        
        // Test name validation
        permission.setName("ab"); // Too short
        assertThrows(IllegalArgumentException.class, () -> {
            permission.validate();
        });

        permission.setName("validpermissionname");
        
        // Test resource validation
        permission.setResource("a".repeat(51)); // Too long
        assertThrows(IllegalArgumentException.class, () -> {
            permission.validate();
        });

        permission.setResource("validresource");
        
        // Test action validation
        permission.setAction("INVALID"); // Invalid action
        assertThrows(IllegalArgumentException.class, () -> {
            permission.validate();
        });

        permission.setAction("READ");
        
        // Test description validation
        permission.setDescription("a".repeat(256)); // Too long
        assertThrows(IllegalArgumentException.class, () -> {
            permission.validate();
        });

        permission.setDescription("Valid description");
        
        // All validations should pass now
        assertDoesNotThrow(() -> {
            permission.validate();
        });
    }

    @Test
    void testValidActions() {
        String[] validActions = {"CREATE", "READ", "UPDATE", "DELETE"};
        
        for (String action : validActions) {
            Permission permission = Permission.builder()
                    .name("TEST_PERMISSION")
                    .resource("TEST")
                    .action(action)
                    .description("Test permission")
                    .build();
            
            assertDoesNotThrow(() -> {
                permission.validate();
            });
        }
    }
} 