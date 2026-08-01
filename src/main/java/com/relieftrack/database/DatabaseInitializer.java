package com.relieftrack.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HexFormat;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        role TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS warehouses (
                        warehouse_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        district TEXT NOT NULL,
                        address TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS relief_items (
                        item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        expiry_date TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS inventory (
                        inventory_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        warehouse_id INTEGER NOT NULL,
                        item_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        minimum_stock INTEGER NOT NULL,

                        FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id),
                        FOREIGN KEY (item_id) REFERENCES relief_items(item_id)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS emergency_requests (
                        request_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        organization TEXT NOT NULL,
                        disaster_type TEXT NOT NULL,
                        item_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        priority TEXT NOT NULL,
                        status TEXT NOT NULL,
                        request_date TEXT NOT NULL,

                        FOREIGN KEY (item_id) REFERENCES relief_items(item_id)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS dispatches (
                        dispatch_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        request_id INTEGER NOT NULL,
                        warehouse_id INTEGER NOT NULL,
                        dispatch_date TEXT NOT NULL,
                        status TEXT NOT NULL,

                        FOREIGN KEY (request_id) REFERENCES emergency_requests(request_id),
                        FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS warehouse_connections (
                        connection_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        source_warehouse INTEGER NOT NULL,
                        destination_warehouse INTEGER NOT NULL,
                        distance REAL NOT NULL,

                        FOREIGN KEY (source_warehouse) REFERENCES warehouses(warehouse_id),
                        FOREIGN KEY (destination_warehouse) REFERENCES warehouses(warehouse_id)
                    );
                    """);

            seedAdminUser(connection);
            System.out.println("✅ Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize database.");
            e.printStackTrace();
        }
    }

    private static void seedAdminUser(Connection connection) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, "admin");

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("⚠️ Admin user already exists. Skipping seeding.");
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.setString(1, "admin");
            insertStatement.setString(2, hashPassword("admin123"));
            insertStatement.setString(3, "System Administrator");
            insertStatement.setString(4, "ADMIN");
            insertStatement.executeUpdate();
        }

        System.out.println("✅ Default admin user seeded.");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}