package com.usermanagement.patterns.factory.db;

public class MySQLConnection implements DatabaseConnection {
    @Override
    public void connect() {
        System.out.println("Connected to MySQL database.");
    }
}

