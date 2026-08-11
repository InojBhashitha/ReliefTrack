package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.enums.Category;
import com.relieftrack.model.ReliefItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReliefItemServiceTest {

    @Test
    void testDuplicateNameItemsAreNotLostInCache(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("dup-name-test.db"));
        DatabaseInitializer.initializeDatabase();

        // Clear seeded demo data
        try (java.sql.Connection conn = com.relieftrack.database.DatabaseManager.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM dispatches");
            stmt.execute("DELETE FROM emergency_requests");
            stmt.execute("DELETE FROM inventory");
            stmt.execute("DELETE FROM relief_items");
        }

        ReliefItemService service = new ReliefItemService();

        // Save two items with the SAME name but different categories/expiry
        ReliefItem item1 = new ReliefItem(0, "Water Pack", Category.WATER, LocalDate.now().plusDays(10));
        ReliefItem item2 = new ReliefItem(0, "Water Pack", Category.WATER, LocalDate.now().plusDays(30));
        service.save(item1);
        service.save(item2);

        // Both items should be in findAll (AVL cache must contain both)
        List<ReliefItem> all = service.findAll();
        long waterPackCount = all.stream().filter(i -> "Water Pack".equals(i.getName())).count();
        assertEquals(2, waterPackCount,
                "Both 'Water Pack' items should be present in the AVL cache; duplicate name must not overwrite.");

        // findByName should return at least one match
        Optional<ReliefItem> found = service.findByName("Water Pack");
        assertTrue(found.isPresent(), "findByName should find a 'Water Pack' item.");
        assertEquals("Water Pack", found.get().getName());

        System.clearProperty("relieftrack.database.url");
    }

    @Test
    void testFindByNameWorksForUniqueNames(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("unique-name-test.db"));
        DatabaseInitializer.initializeDatabase();

        // Clear seeded demo data
        try (java.sql.Connection conn = com.relieftrack.database.DatabaseManager.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM dispatches");
            stmt.execute("DELETE FROM emergency_requests");
            stmt.execute("DELETE FROM inventory");
            stmt.execute("DELETE FROM relief_items");
        }

        ReliefItemService service = new ReliefItemService();

        ReliefItem item1 = new ReliefItem(0, "Blanket", Category.SHELTER, LocalDate.now().plusDays(90));
        ReliefItem item2 = new ReliefItem(0, "Rice Bag", Category.FOOD, LocalDate.now().plusDays(60));
        service.save(item1);
        service.save(item2);

        // Unique name lookup
        Optional<ReliefItem> blanket = service.findByName("Blanket");
        assertTrue(blanket.isPresent());
        assertEquals("Blanket", blanket.get().getName());

        Optional<ReliefItem> rice = service.findByName("Rice Bag");
        assertTrue(rice.isPresent());
        assertEquals("Rice Bag", rice.get().getName());

        // Non-existent name
        Optional<ReliefItem> missing = service.findByName("NonExistent");
        assertTrue(missing.isEmpty());

        System.clearProperty("relieftrack.database.url");
    }
}
