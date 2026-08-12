package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.database.DatabaseManager;
import com.relieftrack.datastructure.graph.Graph;
import com.relieftrack.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseRoutingServiceTest {

    private WarehouseRoutingService routingService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        System.setProperty("relieftrack.database.url",
                "jdbc:sqlite:" + tempDir.resolve("routing-test.db"));
        DatabaseInitializer.initializeDatabase();
        routingService = new WarehouseRoutingService();
        warehouseService = new WarehouseService();
    }

    @Test
    void buildGraphSucceedsAndCalculatesRoute() throws SQLException {
        // Warehouse connections are seeded in demo data by our database initializer:
        // Colombo Central Hub (1) <-> Galle Coastal Depot (2) distance 119.5
        List<Warehouse> warehouses = warehouseService.findAll();
        assertEquals(4, warehouses.size());

        Warehouse hub = warehouses.get(0);
        Warehouse depot = warehouses.get(1);

        List<String> route = routingService.findShortestRoute(hub.getWarehouseId(), depot.getWarehouseId());
        assertEquals(2, route.size());
        assertEquals(hub.getName(), route.get(0));
        assertEquals(depot.getName(), route.get(1));

        String description = routingService.describeRoute(hub.getWarehouseId(), depot.getWarehouseId());
        assertEquals(hub.getName() + " -> " + depot.getName(), description);
    }

    @Test
    void duplicateAndReverseEdgesInDatabaseDoNotCrashGraphBuilder() throws SQLException {
        List<Warehouse> warehouses = warehouseService.findAll();
        Warehouse hub = warehouses.get(0);
        Warehouse depot = warehouses.get(1);

        // Manually insert duplicate connection entries in both directions
        try (Connection conn = DatabaseManager.getConnection()) {
            // First clear existing connections
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM warehouse_connections")) {
                stmt.executeUpdate();
            }

            String sql = "INSERT INTO warehouse_connections (source_warehouse, destination_warehouse, distance) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Forward direction
                stmt.setInt(1, hub.getWarehouseId());
                stmt.setInt(2, depot.getWarehouseId());
                stmt.setDouble(3, 45.2);
                stmt.executeUpdate();

                // Reverse direction (simulates bidirectional storage in DB)
                stmt.setInt(1, depot.getWarehouseId());
                stmt.setInt(2, hub.getWarehouseId());
                stmt.setDouble(3, 45.2);
                stmt.executeUpdate();

                // Duplicate forward direction
                stmt.setInt(1, hub.getWarehouseId());
                stmt.setInt(2, depot.getWarehouseId());
                stmt.setDouble(3, 50.0);
                stmt.executeUpdate();
            }
        }

        // Calling buildWarehouseGraph should run successfully without throwing IllegalArgumentException
        assertDoesNotThrow(() -> {
            Graph graph = routingService.buildWarehouseGraph();
            assertNotNull(graph);
            assertEquals(4, graph.getVertices().size());
        });

        // The shortest route calculation should still work
        List<String> route = routingService.findShortestRoute(hub.getWarehouseId(), depot.getWarehouseId());
        assertEquals(2, route.size());
    }
}
