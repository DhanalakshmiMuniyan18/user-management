package com.usermanagement.patterns.factory.db;

public class DatabaseConnectionFactory {
    public static DatabaseConnection getConnection(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Database type must not be null or empty");
        }
        switch (type) {
            case "MySQL":
                return new MySQLConnection();
            case "PostgreSQL":
                return new PostgreSQLConnection();
            case "Oracle":
                return new OracleConnection();
            default:
                throw new IllegalArgumentException("Unknown database type: " + type);
        }
    }
}
