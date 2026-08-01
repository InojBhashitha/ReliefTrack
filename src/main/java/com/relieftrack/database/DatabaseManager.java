package com.relieftrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.getDatabaseUrl());
    }

}
