package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.database.DatabaseManager;
import com.relieftrack.enums.DispatchStatus;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
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

/**
 * Tests that verify duplicate-dispatch prevention.
 *
 * <p>All tests use a temporary database seeded with demo data so the
 * checked-in production database is never modified.</p>
 */
class DuplicateDispatchTest {

    private DispatchService dispatchService;
    private EmergencyRequestService requestService;
    private WarehouseService warehouseService;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        System.setProperty("relieftrack.database.url",
                "jdbc:sqlite:" + tempDir.resolve("dup-dispatch-test.db"));
        DatabaseInitializer.initializeDatabase();

        dispatchService = new DispatchService();
        requestService = new EmergencyRequestService();
        warehouseService = new WarehouseService();
        inventoryService = new InventoryService();
    }

    /**
     * (a) Scheduling a valid request (with no existing dispatch) once succeeds.
     * Uses the APPROVED request from demo data which has no dispatch.
     */
    @Test
    void schedulingValidRequestOnceSucceeds() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        assertDoesNotThrow(() -> dispatchService.scheduleDispatch(request, warehouse));

        assertEquals(RequestStatus.DISPATCHED,
                requestService.findById(request.getRequestId()).getStatus());
    }

    /**
     * (b) Trying to schedule the same request again is rejected.
     */
    @Test
    void schedulingSameRequestTwiceIsRejected() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        dispatchService.scheduleDispatch(request, warehouse);

        // The stale in-memory object still has APPROVED status — this is exactly
        // the bug scenario: a caller holds an old object and tries again.
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> dispatchService.scheduleDispatch(request, warehouse));

        assertTrue(exception.getMessage().toLowerCase().contains("dispatch")
                || exception.getMessage().toLowerCase().contains("status"),
                "Error message should mention dispatch or status: " + exception.getMessage());
    }

    /**
     * (c) The second attempt does NOT reserve additional inventory.
     */
    @Test
    void rejectedDuplicateDoesNotConsumeInventory() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);
        Inventory inventoryBefore = findMatchingInventory(request, warehouse);

        dispatchService.scheduleDispatch(request, warehouse);

        Inventory inventoryAfterFirst = inventoryService.findById(inventoryBefore.getInventoryId());
        int expectedQuantity = inventoryBefore.getQuantity() - request.getQuantity();
        assertEquals(expectedQuantity, inventoryAfterFirst.getQuantity());

        // Attempt duplicate
        assertThrows(IllegalStateException.class,
                () -> dispatchService.scheduleDispatch(request, warehouse));

        // Inventory unchanged after rejected attempt
        Inventory inventoryAfterSecond = inventoryService.findById(inventoryBefore.getInventoryId());
        assertEquals(expectedQuantity, inventoryAfterSecond.getQuantity(),
                "Inventory must not change after a rejected duplicate dispatch.");
    }

    /**
     * (d) The request status remains DISPATCHED after the rejected second attempt.
     */
    @Test
    void requestStatusUnchangedAfterRejectedDuplicate() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        dispatchService.scheduleDispatch(request, warehouse);

        assertThrows(IllegalStateException.class,
                () -> dispatchService.scheduleDispatch(request, warehouse));

        assertEquals(RequestStatus.DISPATCHED,
                requestService.findById(request.getRequestId()).getStatus(),
                "Request status must remain DISPATCHED after rejected duplicate.");
    }

    /**
     * (e) The database-level unique partial index also prevents duplicate active
     * dispatches even if the application-level check is somehow bypassed.
     */
    @Test
    void databaseConstraintPreventsSecondActiveDispatch() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        dispatchService.scheduleDispatch(request, warehouse);

        // Manually try to insert a second PENDING dispatch directly via SQL,
        // bypassing the service layer, to prove the DB index catches it.
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO dispatches (request_id, warehouse_id, dispatch_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, request.getRequestId());
                stmt.setInt(2, warehouse.getWarehouseId());
                stmt.setString(3, "2026-08-11T12:00:00");
                stmt.setString(4, DispatchStatus.PENDING.name());
                assertThrows(SQLException.class, stmt::executeUpdate,
                        "DB unique index should prevent a second active dispatch.");
            }
        }
    }

    /**
     * Verify that the unique index does NOT block a new dispatch after the
     * previous one has been cancelled (historical dispatch).
     */
    @Test
    void newDispatchAllowedAfterPreviousIsCancelled() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        // First dispatch succeeds
        dispatchService.scheduleDispatch(request, warehouse);

        // Cancel the dispatch — simulating normal lifecycle
        List<Dispatch> dispatches = dispatchService.findAll();
        Dispatch activeDispatch = dispatches.stream()
                .filter(d -> d.getRequest().getRequestId() == request.getRequestId()
                        && d.getStatus() == DispatchStatus.PENDING)
                .findFirst().orElseThrow();
        activeDispatch.setStatus(DispatchStatus.CANCELLED);
        dispatchService.update(activeDispatch);

        // Reset request status back to APPROVED so it can be re-dispatched
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE emergency_requests SET status = ? WHERE request_id = ?")) {
            stmt.setString(1, RequestStatus.APPROVED.name());
            stmt.setInt(2, request.getRequestId());
            stmt.executeUpdate();
        }

        // Second dispatch should now succeed since previous is CANCELLED
        assertDoesNotThrow(() -> dispatchService.scheduleDispatch(request, warehouse));
    }

    /**
     * Verify that the dispatch count increases by exactly 1 (not 2) when
     * a duplicate attempt is rejected.
     */
    @Test
    void dispatchCountIncrementsByOneOnly() throws SQLException {
        EmergencyRequest request = findApprovedRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);
        int countBefore = dispatchService.findAll().size();

        dispatchService.scheduleDispatch(request, warehouse);
        assertThrows(IllegalStateException.class,
                () -> dispatchService.scheduleDispatch(request, warehouse));

        assertEquals(countBefore + 1, dispatchService.findAll().size(),
                "Exactly one dispatch should have been created.");
    }

    /**
     * Verify that a request which already has an active dispatch from demo data
     * (the seeded PENDING dispatch) cannot be dispatched again.
     */
    @Test
    void seededPendingRequestWithExistingDispatchCannotBeRedispatched() throws SQLException {
        EmergencyRequest request = findPendingRequest();
        Warehouse warehouse = warehouseService.findAll().get(0);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> dispatchService.scheduleDispatch(request, warehouse));

        assertTrue(exception.getMessage().toLowerCase().contains("active dispatch"),
                "Error should mention active dispatch: " + exception.getMessage());
    }

    // --- Helpers ---

    private EmergencyRequest findPendingRequest() throws SQLException {
        return requestService.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No PENDING request in demo data."));
    }

    private EmergencyRequest findApprovedRequest() throws SQLException {
        return requestService.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No APPROVED request in demo data."));
    }

    private Inventory findMatchingInventory(EmergencyRequest request, Warehouse warehouse) throws SQLException {
        return inventoryService.findAll().stream()
                .filter(inv -> inv.getWarehouse().getWarehouseId() == warehouse.getWarehouseId()
                        && inv.getReliefItem().getItemId() == request.getReliefItem().getItemId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No matching inventory for request + warehouse."));
    }
}
