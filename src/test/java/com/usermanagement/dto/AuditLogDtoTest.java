package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogDtoTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setAction("LOGIN");
        dto.setDetails("User logged in");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals("LOGIN", dto.getAction());
        assertEquals("User logged in", dto.getDetails());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void canHandleNullFields() {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(null);
        dto.setUserId(null);
        dto.setAction(null);
        dto.setDetails(null);
        dto.setCreatedAt(null);
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getAction());
        assertNull(dto.getDetails());
        assertNull(dto.getCreatedAt());
    }

    @Test
    void toStringIsNotNull() {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setAction("LOGOUT");
        assertNotNull(dto.toString());
    }
} 