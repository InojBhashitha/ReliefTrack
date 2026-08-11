package com.relieftrack.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializerTest {

    /**
     * Verifies that seedDemoData() produces valid foreign-key relationships
     * even when AUTOINCREMENT IDs do not start from 1.
     *
     * Strategy:
     *  1. Initialize the database schema (creates tables + seeds demo data).
     *  2. Delete all seeded data in FK-safe order.
     *  3. Insert and delete throwaway rows to advance AUTOINCREMENT counters.
     *  4. Re-seed by calling initializeDatabase() again (guard checks pass
     *     because tables are now empty).
     *  5. Run PRAGMA foreign_key_check — it returns rows only if any FK is broken.
     */
    @Test
    void seedDemoDataUsesGeneratedIdsNotHardCoded(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("seed-test.db"));
        try {
            // First initialization — creates schema and seeds demo data
            DatabaseInitializer.initializeDatabase();

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Delete all seeded data in FK-safe order
                stmt.execute("DELETE FROM dispatches");
                stmt.execute("DELETE FROM emergency_requests");
                stmt.execute("DELETE FROM inventory");
                stmt.execute("DELETE FROM warehouse_connections");
                stmt.execute("DELETE FROM relief_items");
                stmt.execute("DELETE FROM warehouses");
                stmt.execute("DELETE FROM users");

                // Insert and delete throwaway rows to advance AUTOINCREMENT counters
                // so that the next real IDs will NOT be 1 or 2.
                for (int i = 0; i < 5; i++) {
                    stmt.executeUpdate("INSERT INTO warehouses (name, district, address) VALUES ('tmp', 'tmp', 'tmp')");
                    stmt.executeUpdate("INSERT INTO relief_items (name, category, expiry_date) VALUES ('tmp', 'WATER', '2099-01-01')");
                    stmt.executeUpdate("INSERT INTO emergency_requests (organization, disaster_type, item_id, quantity, priority, status, request_date) " +
                            "VALUES ('tmp', 'FLOOD', (SELECT MAX(item_id) FROM relief_items), 1, 'LOW', 'PENDING', '2026-01-01')");
                }
                stmt.execute("DELETE FROM emergency_requests");
                stmt.execute("DELETE FROM relief_items");
                stmt.execute("DELETE FROM warehouses");

                // Verify counters have advanced past 1
                try (ResultSet rs = stmt.executeQuery("SELECT seq FROM sqlite_sequence WHERE name = 'warehouses'")) {
                    assertTrue(rs.next(), "sqlite_sequence should have an entry for warehouses");
                    assertTrue(rs.getInt(1) > 2, "warehouse AUTOINCREMENT counter should be > 2, was " + rs.getInt(1));
                }
            }

            // Second initialization — tables exist but are empty, so seeding runs again
            DatabaseInitializer.initializeDatabase();

            // Verify all foreign-key relationships are valid
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet fkErrors = stmt.executeQuery("PRAGMA foreign_key_check")) {

                if (fkErrors.next()) {
                    StringBuilder sb = new StringBuilder("Broken FK relationships found:\n");
                    do {
                        sb.append("  table=").append(fkErrors.getString(1))
                          .append(" rowid=").append(fkErrors.getLong(2))
                          .append(" parent=").append(fkErrors.getString(3))
                          .append(" fkid=").append(fkErrors.getInt(4))
                          .append("\n");
                    } while (fkErrors.next());
                    fail(sb.toString());
                }
            }

            // Verify expected record counts
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {

                assertCount(stmt, "warehouses", 2);
                assertCount(stmt, "relief_items", 2);
                assertCount(stmt, "inventory", 3);
                assertCount(stmt, "emergency_requests", 2);
                assertCount(stmt, "dispatches", 1);
            }
        } finally {
            System.clearProperty("relieftrack.database.url");
        }
    }

    /**
     * Verifies that calling initializeDatabase() twice does not duplicate demo data.
     */
    @Test
    void initializeDatabaseIsIdempotent(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("idempotent-test.db"));
        try {
            DatabaseInitializer.initializeDatabase();
            DatabaseInitializer.initializeDatabase();

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {

                assertCount(stmt, "warehouses", 2);
                assertCount(stmt, "relief_items", 2);
                assertCount(stmt, "inventory", 3);
                assertCount(stmt, "emergency_requests", 2);
                assertCount(stmt, "dispatches", 1);
                assertCount(stmt, "users", 1);
            }
        } finally {
            System.clearProperty("relieftrack.database.url");
        }
    }

    private static void assertCount(Statement stmt, String table, int expected) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            assertTrue(rs.next());
            assertEquals(expected, rs.getInt(1), table + " row count");
        }
    }
}
