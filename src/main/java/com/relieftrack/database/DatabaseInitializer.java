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

            // Prevent more than one active (PENDING or IN_TRANSIT) dispatch per request.
            // Completed or cancelled dispatches are not constrained, allowing dispatch history.
            statement.execute("""
                    CREATE UNIQUE INDEX IF NOT EXISTS idx_dispatches_active_request
                    ON dispatches(request_id)
                    WHERE status IN ('PENDING', 'IN_TRANSIT');
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
            int colomboHubId;
            int galleDepotId;
            int kandyStationId;
            int anuradhapuraDepotId;
            try (PreparedStatement warehouseInsert = connection.prepareStatement(
                    "INSERT INTO warehouses (name, district, address) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                warehouseInsert.setString(1, "Colombo Central Hub");
                warehouseInsert.setString(2, "Colombo");
                warehouseInsert.setString(3, "100 D.R. Wijewardena Mawatha, Colombo 10");
                warehouseInsert.executeUpdate();
                colomboHubId = getGeneratedId(warehouseInsert);

                warehouseInsert.setString(1, "Galle Coastal Depot");
                warehouseInsert.setString(2, "Galle");
                warehouseInsert.setString(3, "12 Wackwella Road, Galle");
                warehouseInsert.executeUpdate();
                galleDepotId = getGeneratedId(warehouseInsert);

                warehouseInsert.setString(1, "Kandy Hill Country Station");
                warehouseInsert.setString(2, "Kandy");
                warehouseInsert.setString(3, "45 Peradeniya Road, Kandy");
                warehouseInsert.executeUpdate();
                kandyStationId = getGeneratedId(warehouseInsert);

                warehouseInsert.setString(1, "Anuradhapura Dry Zone Depot");
                warehouseInsert.setString(2, "Anuradhapura");
                warehouseInsert.setString(3, "88 Maitripala Senanayake Mawatha, Anuradhapura");
                warehouseInsert.executeUpdate();
                anuradhapuraDepotId = getGeneratedId(warehouseInsert);
            }

            // --- Relief Items ---
            int waterPackId;
            int medicalKitId;
            int rationsPackId;
            int tentId;
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

                itemInsert.setString(1, "Dry Rations Pack");
                itemInsert.setString(2, "FOOD");
                itemInsert.setString(3, "2027-06-30");
                itemInsert.executeUpdate();
                rationsPackId = getGeneratedId(itemInsert);

                itemInsert.setString(1, "Emergency Tent");
                itemInsert.setString(2, "SHELTER");
                itemInsert.setString(3, "2029-12-31");
                itemInsert.executeUpdate();
                tentId = getGeneratedId(itemInsert);
            }

            // --- Inventory (uses warehouse + item IDs) ---
            try (PreparedStatement inventoryInsert = connection.prepareStatement(
                    "INSERT INTO inventory (warehouse_id, item_id, quantity, minimum_stock) VALUES (?, ?, ?, ?)")) {

                // Colombo Central Hub Inventory
                inventoryInsert.setInt(1, colomboHubId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 500);
                inventoryInsert.setInt(4, 100);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, colomboHubId);
                inventoryInsert.setInt(2, medicalKitId);
                inventoryInsert.setInt(3, 150);
                inventoryInsert.setInt(4, 30);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, colomboHubId);
                inventoryInsert.setInt(2, rationsPackId);
                inventoryInsert.setInt(3, 300);
                inventoryInsert.setInt(4, 50);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, colomboHubId);
                inventoryInsert.setInt(2, tentId);
                inventoryInsert.setInt(3, 80);
                inventoryInsert.setInt(4, 20);
                inventoryInsert.executeUpdate();

                // Galle Coastal Depot Inventory
                inventoryInsert.setInt(1, galleDepotId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 200);
                inventoryInsert.setInt(4, 50);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, galleDepotId);
                inventoryInsert.setInt(2, rationsPackId);
                inventoryInsert.setInt(3, 150);
                inventoryInsert.setInt(4, 30);
                inventoryInsert.executeUpdate();

                // Kandy Hill Country Station Inventory
                inventoryInsert.setInt(1, kandyStationId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 150);
                inventoryInsert.setInt(4, 40);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, kandyStationId);
                inventoryInsert.setInt(2, medicalKitId);
                inventoryInsert.setInt(3, 60);
                inventoryInsert.setInt(4, 20);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, kandyStationId);
                inventoryInsert.setInt(2, rationsPackId);
                inventoryInsert.setInt(3, 120);
                inventoryInsert.setInt(4, 30);
                inventoryInsert.executeUpdate();

                // Anuradhapura Dry Zone Depot Inventory
                inventoryInsert.setInt(1, anuradhapuraDepotId);
                inventoryInsert.setInt(2, waterPackId);
                inventoryInsert.setInt(3, 100);
                inventoryInsert.setInt(4, 30);
                inventoryInsert.executeUpdate();

                inventoryInsert.setInt(1, anuradhapuraDepotId);
                inventoryInsert.setInt(2, rationsPackId);
                inventoryInsert.setInt(3, 80);
                inventoryInsert.setInt(4, 20);
                inventoryInsert.executeUpdate();
            }

            // --- Emergency Requests (uses item IDs) ---
            int sarvodayaFloodId;
            int redCrossLandslideId;
            int dmcDroughtId;
            try (PreparedStatement requestInsert = connection.prepareStatement(
                    "INSERT INTO emergency_requests (organization, disaster_type, item_id, quantity, priority, status, request_date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                requestInsert.setString(1, "Sarvodaya");
                requestInsert.setString(2, "FLOOD");
                requestInsert.setInt(3, waterPackId);
                requestInsert.setInt(4, 150);
                requestInsert.setString(5, "CRITICAL");
                requestInsert.setString(6, "PENDING");
                requestInsert.setString(7, "2026-08-10T08:30:00");
                requestInsert.executeUpdate();
                sarvodayaFloodId = getGeneratedId(requestInsert);

                requestInsert.setString(1, "SL Red Cross");
                requestInsert.setString(2, "LANDSLIDE");
                requestInsert.setInt(3, medicalKitId);
                requestInsert.setInt(4, 25);
                requestInsert.setString(5, "HIGH");
                requestInsert.setString(6, "DISPATCHED");
                requestInsert.setString(7, "2026-08-11T09:15:00");
                requestInsert.executeUpdate();
                redCrossLandslideId = getGeneratedId(requestInsert);

                requestInsert.setString(1, "DMC Sri Lanka");
                requestInsert.setString(2, "DROUGHT");
                requestInsert.setInt(3, rationsPackId);
                requestInsert.setInt(4, 50);
                requestInsert.setString(5, "MEDIUM");
                requestInsert.setString(6, "APPROVED");
                requestInsert.setString(7, "2026-08-12T10:00:00");
                requestInsert.executeUpdate();
                dmcDroughtId = getGeneratedId(requestInsert);
            }

            // --- Dispatches (uses request + warehouse IDs) ---
            try (PreparedStatement dispatchInsert = connection.prepareStatement(
                    "INSERT INTO dispatches (request_id, warehouse_id, dispatch_date, status) VALUES (?, ?, ?, ?)")) {

                dispatchInsert.setInt(1, sarvodayaFloodId);
                dispatchInsert.setInt(2, colomboHubId);
                dispatchInsert.setString(3, "2026-08-12T11:00:00");
                dispatchInsert.setString(4, "PENDING");
                dispatchInsert.executeUpdate();

                dispatchInsert.setInt(1, redCrossLandslideId);
                dispatchInsert.setInt(2, kandyStationId);
                dispatchInsert.setString(3, "2026-08-11T12:00:00");
                dispatchInsert.setString(4, "SHIPPED");
                dispatchInsert.executeUpdate();
            }

            // --- Warehouse Connections (uses warehouse IDs) ---
            try (PreparedStatement connectionInsert = connection.prepareStatement(
                    "INSERT INTO warehouse_connections (source_warehouse, destination_warehouse, distance) VALUES (?, ?, ?)")) {
                // Colombo <-> Galle
                connectionInsert.setInt(1, colomboHubId);
                connectionInsert.setInt(2, galleDepotId);
                connectionInsert.setDouble(3, 119.5);
                connectionInsert.executeUpdate();

                // Colombo <-> Kandy
                connectionInsert.setInt(1, colomboHubId);
                connectionInsert.setInt(2, kandyStationId);
                connectionInsert.setDouble(3, 115.2);
                connectionInsert.executeUpdate();

                // Kandy <-> Anuradhapura
                connectionInsert.setInt(1, kandyStationId);
                connectionInsert.setInt(2, anuradhapuraDepotId);
                connectionInsert.setDouble(3, 136.0);
                connectionInsert.executeUpdate();

                // Colombo <-> Anuradhapura
                connectionInsert.setInt(1, colomboHubId);
                connectionInsert.setInt(2, anuradhapuraDepotId);
                connectionInsert.setDouble(3, 201.8);
                connectionInsert.executeUpdate();
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
