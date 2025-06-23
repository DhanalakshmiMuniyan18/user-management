package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PermissionDtoTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        PermissionDto dto = new PermissionDto();
        dto.setId(1L);
        dto.setName("PERM_READ");
        dto.setDescription("Read permission");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(1L, dto.getId());
        assertEquals("PERM_READ", dto.getName());
        assertEquals("Read permission", dto.getDescription());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void canHandleNullFields() {
        PermissionDto dto = new PermissionDto();
        dto.setId(null);
        dto.setName(null);
        dto.setDescription(null);
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void toStringIsNotNull() {
        PermissionDto dto = new PermissionDto();
        dto.setId(1L);
        dto.setName("PERM_WRITE");
        assertNotNull(dto.toString());
    }
} 