package com.usermanagement.dto;

import com.usermanagement.model.entity.AccessRequest.Status;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccessRequestDtoTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        AccessRequestDto dto = new AccessRequestDto();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setRoleId(3L);
        dto.setStatus(Status.APPROVED);
        dto.setReason("Need access");
        dto.setResponseMessage("Granted");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals(3L, dto.getRoleId());
        assertEquals(Status.APPROVED, dto.getStatus());
        assertEquals("Need access", dto.getReason());
        assertEquals("Granted", dto.getResponseMessage());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void canHandleNullFields() {
        AccessRequestDto dto = new AccessRequestDto();
        dto.setId(null);
        dto.setUserId(null);
        dto.setRoleId(null);
        dto.setStatus(null);
        dto.setReason(null);
        dto.setResponseMessage(null);
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getRoleId());
        assertNull(dto.getStatus());
        assertNull(dto.getReason());
        assertNull(dto.getResponseMessage());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void toStringIsNotNull() {
        AccessRequestDto dto = new AccessRequestDto();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setRoleId(3L);
        assertNotNull(dto.toString());
    }
} 