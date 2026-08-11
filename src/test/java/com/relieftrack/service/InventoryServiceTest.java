package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.enums.Category;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.model.Warehouse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @Test
    void testInventoryPrefixSearchMatchesCaseInsensitively(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("inventory-test.db"));
        DatabaseInitializer.initializeDatabase();

        // Clear seeded demo data for clean test execution
        try (java.sql.Connection conn = com.relieftrack.database.DatabaseManager.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM dispatches");
            stmt.execute("DELETE FROM emergency_requests");
            stmt.execute("DELETE FROM inventory");
            stmt.execute("DELETE FROM relief_items");
            stmt.execute("DELETE FROM warehouses");
        }

        WarehouseService warehouseService = new WarehouseService();
        Warehouse warehouse = new Warehouse(0, "Test Warehouse", "Colombo", "123 Street");
        warehouseService.save(warehouse);
        try (java.sql.Connection conn = com.relieftrack.database.DatabaseManager.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT MAX(warehouse_id) FROM warehouses")) {
            if (rs.next()) {
                warehouse.setWarehouseId(rs.getInt(1));
            }
        }

        ReliefItemService itemService = new ReliefItemService();
        ReliefItem item1 = new ReliefItem(0, "Water Pack", Category.WATER, LocalDate.now().plusDays(10));
        ReliefItem item2 = new ReliefItem(0, "Rice Bag", Category.FOOD, LocalDate.now().plusDays(20));
        itemService.save(item1);
        itemService.save(item2);

        InventoryService inventoryService = new InventoryService();
        Inventory inv1 = new Inventory(0, warehouse, item1, 100, 10);
        Inventory inv2 = new Inventory(0, warehouse, item2, 200, 20);

        inventoryService.save(inv1);
        inventoryService.save(inv2);


        // Search "water" should find "Water Pack"
        List<Inventory> results = inventoryService.search("water");
        assertEquals(1, results.size());
        assertEquals("Water Pack", results.get(0).getReliefItem().getName());

        // Search "WATER" should also find "Water Pack"
        results = inventoryService.search("WATER");
        assertEquals(1, results.size());

        // Search "rice" should find "Rice Bag"
        results = inventoryService.search("rice");
        assertEquals(1, results.size());
        assertEquals("Rice Bag", results.get(0).getReliefItem().getName());

        // Search "nonexistent" should find nothing
        results = inventoryService.search("nonexistent");
        assertEquals(0, results.size());

        System.clearProperty("relieftrack.database.url");
    }
}
