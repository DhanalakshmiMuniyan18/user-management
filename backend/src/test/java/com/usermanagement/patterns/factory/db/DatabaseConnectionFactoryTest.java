package com.usermanagement.patterns.factory.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseConnectionFactory.
 * Covers valid, invalid, null, empty, and whitespace-only input cases.
 */
public class DatabaseConnectionFactoryTest {
    @ParameterizedTest
    @CsvSource({
        "MySQL,com.usermanagement.patterns.factory.db.MySQLConnection",
        "PostgreSQL,com.usermanagement.patterns.factory.db.PostgreSQLConnection",
        "Oracle,com.usermanagement.patterns.factory.db.OracleConnection"
    })
    void testValidConnections(String type, String expectedClassName) {
        DatabaseConnection conn = DatabaseConnectionFactory.getConnection(type);
        assertEquals(expectedClassName, conn.getClass().getName(),
            () -> "Expected instance of " + expectedClassName + " for type " + type);
    }

    @Test
    void testUnknownConnection() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseConnectionFactory.getConnection("SQLite");
        });
        assertTrue(exception.getMessage().contains("Unknown database type"),
            "Exception message should mention unknown database type");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testNullOrEmptyConnectionType(String type) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseConnectionFactory.getConnection(type);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unknown"),
            "Exception message should mention unknown database type");
    }

    /**
     * Test that whitespace-only input throws an exception.
     */
    @ParameterizedTest
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void testWhitespaceOnlyConnectionType(String type) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DatabaseConnectionFactory.getConnection(type);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unknown"),
            "Exception message should mention unknown database type");
    }
}
