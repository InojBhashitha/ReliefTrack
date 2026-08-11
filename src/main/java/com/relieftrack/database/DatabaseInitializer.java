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
        // Ensure data directory exists to avoid SQLITE_CANTOPEN
        java.io.File dbFolder = new java.io.File(DatabaseConfig.DATABASE_FOLDER);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

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

            // Correct the legacy category value used by earlier demo databases.
            statement.executeUpdate("UPDATE relief_items SET category = 'MEDICINE' WHERE category = 'MEDICAL'");

            seedAdminUser(connection);
            seedDemoData(connection);
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

    private static void seedDemoData(Connection connection) throws SQLException {
        try (PreparedStatement countWarehouses = connection.prepareStatement("SELECT COUNT(*) FROM warehouses")) {
            try (ResultSet resultSet = countWarehouses.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return;
                }
            }
        }

        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // --- Warehouses ---
            int centralHubId;
            int coastalDepotId;
            try (PreparedStatement warehouseInsert = connection.prepareStatement(
                    "INSERT INTO warehouses (name, district, address) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                warehouseInsert.setString(1, "Central Hub");
                warehouseInsert.setString(2, "North District");
                warehouseInsert.setString(3, "12 Relief Avenue");
                warehouseInsert.executeUpdate();
                centralHubId = getGeneratedId(warehouseInsert);

                warehouseInsert.setString(1, "Coastal Depot");
                warehouseInsert.setString(2, "Coastal Zone");
                warehouseInsert.setString(3, "8 Harbor Road");
                warehouseInsert.executeUpdate();
                coastalDepotId = getGeneratedId(warehouseInsert);
            }

            // --- Relief Items ---
            int waterPackId;
            int medicalKitId;
            try (PreparedStatement itemInsert = connection.prepareStatement(
                    "INSERT INTO relief_items (name, category, expiry_date) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                itemInsert.setString(1, "Water Pack");
                itemInsert.setString(2, "WATER");
                itemInsert.setString(3, "2027-12-31");
                itemInsert.executeUpdate();
                waterPackId = getGeneratedId(itemInsert);

                itemInsert.setString(1, "Medical Kit");
                itemInsert.setString(2, "MEDICINE");
                itemInsert.setString(3, "2027-11-15");
                itemInsert.executeUpdate();
                medicalKitId = getGeneratedId(itemInsert);
            }

            // --- Inventory (uses warehouse + item IDs) ---
            try (PreparedStatement inventoryInsert = connection.prepareStatement(
                    "INSERT INTO inventory (warehouse_id, item_id, quantity, minimum_stock) VALUES (?, ?, ?, ?)")) {

                inventoryInsert.setInt(1, centralHubId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 120);
                inventoryInsert.setInt(4, 40);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, centralHubId);
                inventoryInsert.setInt(2, medicalKitId);
                inventoryInsert.setInt(3, 45);
                inventoryInsert.setInt(4, 15);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, coastalDepotId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 80);
                inventoryInsert.setInt(4, 25);
                inventoryInsert.executeUpdate();
            }

            // --- Emergency Requests (uses item IDs) ---
            int redCrossRequestId;
            try (PreparedStatement requestInsert = connection.prepareStatement(
                    "INSERT INTO emergency_requests (organization, disaster_type, item_id, quantity, priority, status, request_date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                requestInsert.setString(1, "Red Cross");
                requestInsert.setString(2, "FLOOD");
                requestInsert.setInt(3, waterPackId);
                requestInsert.setInt(4, 30);
                requestInsert.setString(5, "HIGH");
                requestInsert.setString(6, "PENDING");
                requestInsert.setString(7, "2026-08-01T09:30:00");
                requestInsert.executeUpdate();
                redCrossRequestId = getGeneratedId(requestInsert);

                requestInsert.setString(1, "Aid Alliance");
                requestInsert.setString(2, "EARTHQUAKE");
                requestInsert.setInt(3, medicalKitId);
                requestInsert.setInt(4, 10);
                requestInsert.setString(5, "MEDIUM");
                requestInsert.setString(6, "APPROVED");
                requestInsert.setString(7, "2026-08-01T10:00:00");
                requestInsert.executeUpdate();
            }

            // --- Dispatches (uses request + warehouse IDs) ---
            try (PreparedStatement dispatchInsert = connection.prepareStatement(
                    "INSERT INTO dispatches (request_id, warehouse_id, dispatch_date, status) VALUES (?, ?, ?, ?)")) {

                dispatchInsert.setInt(1, redCrossRequestId);
                dispatchInsert.setInt(2, centralHubId);
                dispatchInsert.setString(3, "2026-08-01T11:15:00");
                dispatchInsert.setString(4, "PENDING");
                dispatchInsert.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static int getGeneratedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve generated key from INSERT.");
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
