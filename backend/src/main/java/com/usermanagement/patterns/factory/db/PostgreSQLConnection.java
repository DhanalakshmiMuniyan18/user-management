package com.usermanagement.patterns.factory.db;

public class PostgreSQLConnection implements DatabaseConnection {
    @Override
    public void connect() {
        System.out.println("Connected to PostgreSQL database.");
    }
}

