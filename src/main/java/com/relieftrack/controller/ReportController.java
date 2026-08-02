package com.relieftrack.controller;

import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
import com.relieftrack.service.DispatchService;
import com.relieftrack.service.EmergencyRequestService;
import com.relieftrack.service.InventoryService;
import com.relieftrack.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.List;

public class ReportController {

    private final ReportService reportService = new ReportService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final DispatchService dispatchService = new DispatchService();
    private final InventoryService inventoryService = new InventoryService();

    @FXML
    private Label summaryLabel;

    @FXML
    private Label inventoryStat;

    @FXML
    private Label warehouseStat;

    @FXML
    private Label requestStat;

    @FXML
    private Label dispatchStat;

    @FXML
    private Label pendingRequestStat;

    @FXML
    private Label lowStockStat;

    @FXML
    private ListView<String> reportList;

    @FXML
    public void initialize() {
        try {
            ReportService.ReportSummary summary = reportService.getSummary();
            List<EmergencyRequest> requests = emergencyRequestService.findAll();
            List<Dispatch> dispatches = dispatchService.findAll();
            List<Inventory> lowStockItems = inventoryService.findLowStockItems();

            inventoryStat.setText(String.valueOf(summary.getInventoryCount()));
            warehouseStat.setText(String.valueOf(summary.getWarehouseCount()));
            requestStat.setText(String.valueOf(summary.getRequestCount()));
            dispatchStat.setText(String.valueOf(summary.getDispatchCount()));
            pendingRequestStat.setText(String.valueOf(countPending(requests)));
            lowStockStat.setText(String.valueOf(lowStockItems.size()));

            summaryLabel.setText("Operational summary generated successfully.");

            ObservableList<String> items = FXCollections.observableArrayList();
            items.add("Active inventory items: " + summary.getInventoryCount());
            items.add("Operational warehouses: " + summary.getWarehouseCount());
            items.add("Pending/approved requests: " + countPending(requests));
            items.add("Dispatched requests: " + dispatches.size());
            items.add("Low stock alerts: " + lowStockItems.size());
            reportList.setItems(items);
        } catch (SQLException | RuntimeException e) {
            summaryLabel.setText("Unable to generate report summary.");
            if (inventoryStat != null) inventoryStat.setText("0");
            if (warehouseStat != null) warehouseStat.setText("0");
            if (requestStat != null) requestStat.setText("0");
            if (dispatchStat != null) dispatchStat.setText("0");
            if (pendingRequestStat != null) pendingRequestStat.setText("0");
            if (lowStockStat != null) lowStockStat.setText("0");
            if (reportList != null) reportList.setItems(FXCollections.observableArrayList("Unable to load report data."));
            e.printStackTrace();
        }
    }

    private int countPending(List<EmergencyRequest> requests) {
        return (int) requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING || request.getStatus() == RequestStatus.APPROVED)
                .count();
    }
}
