package com.relieftrack.service;

import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.Warehouse;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final InventoryService inventoryService = new InventoryService();
    private final WarehouseService warehouseService = new WarehouseService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final DispatchService dispatchService = new DispatchService();

    public ReportSummary getSummary() throws SQLException {
        List<Inventory> inventory = inventoryService.findAll();
        List<Warehouse> warehouses = warehouseService.findAll();
        List<EmergencyRequest> requests = emergencyRequestService.findAll();
        List<Dispatch> dispatches = dispatchService.findAll();

        return new ReportSummary(
                inventory.size(),
                warehouses.size(),
                requests.size(),
                dispatches.size()
        );
    }

    public static class ReportSummary {
        private final int inventoryCount;
        private final int warehouseCount;
        private final int requestCount;
        private final int dispatchCount;

        public ReportSummary(int inventoryCount, int warehouseCount, int requestCount, int dispatchCount) {
            this.inventoryCount = inventoryCount;
            this.warehouseCount = warehouseCount;
            this.requestCount = requestCount;
            this.dispatchCount = dispatchCount;
        }

        public int getInventoryCount() {
            return inventoryCount;
        }

        public int getWarehouseCount() {
            return warehouseCount;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public int getDispatchCount() {
            return dispatchCount;
        }
    }
}
