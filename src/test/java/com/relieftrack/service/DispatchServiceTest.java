package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.Warehouse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DispatchServiceTest {

    @Test
    void schedulingDispatchReservesStockAndUpdatesRequestStatus(@TempDir Path temporaryDirectory) throws Exception {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + temporaryDirectory.resolve("dispatch-test.db"));
        DatabaseInitializer.initializeDatabase();

        EmergencyRequestService requestService = new EmergencyRequestService();
        WarehouseService warehouseService = new WarehouseService();
        InventoryService inventoryService = new InventoryService();
        DispatchService dispatchService = new DispatchService();

        EmergencyRequest request = requestService.findAll().stream()
                .filter(candidate -> candidate.getStatus() == RequestStatus.APPROVED)
                .findFirst().orElseThrow();
        Warehouse warehouse = warehouseService.findAll().get(0);
        Inventory inventoryBefore = inventoryService.findAll().stream()
                .filter(inventory -> inventory.getWarehouse().getWarehouseId() == warehouse.getWarehouseId()
                        && inventory.getReliefItem().getItemId() == request.getReliefItem().getItemId())
                .findFirst().orElseThrow();
        int dispatchCountBefore = dispatchService.findAll().size();

        dispatchService.scheduleDispatch(request, warehouse);

        Inventory inventoryAfter = inventoryService.findById(inventoryBefore.getInventoryId());
        assertEquals(inventoryBefore.getQuantity() - request.getQuantity(), inventoryAfter.getQuantity());
        assertEquals(RequestStatus.DISPATCHED, requestService.findById(request.getRequestId()).getStatus());
        assertEquals(dispatchCountBefore + 1, dispatchService.findAll().size());
        System.clearProperty("relieftrack.database.url");
    }
}
