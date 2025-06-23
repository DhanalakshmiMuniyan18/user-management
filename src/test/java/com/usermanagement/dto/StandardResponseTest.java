package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class StandardResponseTest {

    @Test
    void successFactoryMethod_ShouldSetDataAndCorrelationId() {
        String data = "payload";
        String correlationId = "corr-123";
        StandardResponse<String> response = StandardResponse.success(data, correlationId);
        assertEquals(data, response.data());
        assertEquals(correlationId, response.correlationId());
        assertNull(response.error());
    }

    @Test
    void errorFactoryMethod_ShouldSetErrorAndCorrelationId() {
        String error = "Something went wrong";
        String correlationId = "corr-456";
        StandardResponse<String> response = StandardResponse.error(error, correlationId);
        assertNull(response.data());
        assertEquals(correlationId, response.correlationId());
        assertEquals(error, response.error());
    }

    @Test
    void recordEqualityAndImmutability() {
        StandardResponse<String> r1 = StandardResponse.success("foo", "id1");
        StandardResponse<String> r2 = StandardResponse.success("foo", "id1");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotSame(r1, r2); // records are immutable, but not the same instance
    }

    @Test
    void nullHandling() {
        StandardResponse<String> response = new StandardResponse<>(null, null, null);
        assertNull(response.data());
        assertNull(response.correlationId());
        assertNull(response.error());
    }

    @Test
    void jsonSerializationDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        StandardResponse<String> original = StandardResponse.success("bar", "cid-789");
        String json = mapper.writeValueAsString(original);
        StandardResponse deserialized = mapper.readValue(json, StandardResponse.class);
        assertEquals(original, deserialized);
    }
} 