package com.usermanagement.patterns.factory.db;

public class OracleConnection implements DatabaseConnection {
    @Override
    public void connect() {
        System.out.println("Connected to Oracle database.");
    }
}

