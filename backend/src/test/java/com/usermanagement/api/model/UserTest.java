package com.usermanagement.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .enabled(true)
                .emailVerified(false)
                .build();

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("1234567890", user.getPhoneNumber());
        assertTrue(user.isEnabled());
        assertFalse(user.isEmailVerified());
    }

    @Test
    void testAddAndRemoveRole() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        user.addRole("ROLE_ADMIN");
        assertTrue(user.getRoles().contains("ROLE_ADMIN"));
        assertEquals(1, user.getRoles().size());

        user.removeRole("ROLE_ADMIN");
        assertFalse(user.getRoles().contains("ROLE_ADMIN"));
        assertEquals(0, user.getRoles().size());
    }

    @Test
    void testUserValidation() {
        User user = new User();
        
        // Test username validation
        user.setUsername("ab"); // Too short
        assertThrows(IllegalArgumentException.class, () -> {
            user.validate();
        });

        user.setUsername("validusername");
        
        // Test email validation
        user.setEmail("invalid-email"); // Invalid email format
        assertThrows(IllegalArgumentException.class, () -> {
            user.validate();
        });

        user.setEmail("valid@example.com");
        
        // Test phone number validation
        user.setPhoneNumber("123"); // Too short
        assertThrows(IllegalArgumentException.class, () -> {
            user.validate();
        });

        user.setPhoneNumber("1234567890");
        
        // Test name validation
        user.setFirstName(""); // Empty first name
        assertThrows(IllegalArgumentException.class, () -> {
            user.validate();
        });

        user.setFirstName("Valid");
        user.setLastName(""); // Empty last name
        assertThrows(IllegalArgumentException.class, () -> {
            user.validate();
        });

        user.setLastName("Name");
        
        // All validations should pass now
        assertDoesNotThrow(() -> {
            user.validate();
        });
    }
} 