package com.relieftrack.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement()) {

            // Users
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        role TEXT NOT NULL
                    );
                    """);

            // Warehouses
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS warehouses (
                        warehouse_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        district TEXT NOT NULL,
                        address TEXT NOT NULL
                    );
                    """);

            // Relief Items
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS relief_items (
                        item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        expiry_date TEXT NOT NULL
                    );
                    """);

            // Inventory
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

            // Emergency Requests
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

            // Dispatches
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

            // Warehouse Connections (Graph)
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

            System.out.println("✅ Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize database.");
            e.printStackTrace();
        }
    }
}