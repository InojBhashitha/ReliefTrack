package com.relieftrack.controller;

import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
import com.relieftrack.service.EmergencyRequestService;
import com.relieftrack.service.InventoryService;
import com.relieftrack.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class HomeController {

    private final ReportService reportService = new ReportService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final InventoryService inventoryService = new InventoryService();

    @FXML
    private Label pageTitle;

    @FXML
    private Label inventoryCountLabel;

    @FXML
    private Label openRequestCountLabel;

    @FXML
    private Label warehouseCountLabel;

    @FXML
    private Label dispatchRateLabel;

    @FXML
    private TableView<EmergencyRequest> requestTable;

    @FXML
    private TableColumn<EmergencyRequest, Integer> requestIdColumn;

    @FXML
    private TableColumn<EmergencyRequest, String> requestOrganizationColumn;

    @FXML
    private TableColumn<EmergencyRequest, String> requestPriorityColumn;

    @FXML
    private TableColumn<EmergencyRequest, String> requestStatusColumn;

    @FXML
    private ListView<String> alertsList;

    @FXML
    public void initialize() {
        configureColumns();
        loadDashboardData();
    }

    private void configureColumns() {
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        requestOrganizationColumn.setCellValueFactory(new PropertyValueFactory<>("organization"));
        requestPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDashboardData() {
        try {
            ReportService.ReportSummary summary = reportService.getSummary();
            List<EmergencyRequest> requests = emergencyRequestService.findAll();
            List<Inventory> lowStockItems = inventoryService.findLowStockItems();

            inventoryCountLabel.setText(String.valueOf(summary.getInventoryCount()));
            openRequestCountLabel.setText(String.valueOf(countOpenRequests(requests)));
            warehouseCountLabel.setText(String.valueOf(summary.getWarehouseCount()));
            dispatchRateLabel.setText(formatDispatchRate(summary.getDispatchCount(), requests.size()));

            if (pageTitle != null) {
                pageTitle.setText("Operations Dashboard");
            }

            ObservableList<EmergencyRequest> requestItems = FXCollections.observableArrayList(requests);
            requestTable.setItems(requestItems);

            ObservableList<String> alerts = FXCollections.observableArrayList();
            if (lowStockItems.isEmpty()) {
                alerts.add("No critical low-stock alerts at the moment.");
            } else {
                for (Inventory item : lowStockItems) {
                    alerts.add(item.getReliefItem().getName() + " is below minimum stock at " + item.getWarehouse().getName());
                }
            }

            alerts.add("Dispatches recorded: " + summary.getDispatchCount());
            alertsList.setItems(alerts);
        } catch (SQLException e) {
            inventoryCountLabel.setText("-" );
            openRequestCountLabel.setText("-");
            warehouseCountLabel.setText("-");
            dispatchRateLabel.setText("-");
            requestTable.setItems(FXCollections.emptyObservableList());
            alertsList.setItems(FXCollections.observableArrayList("Unable to load dashboard data."));
            e.printStackTrace();
        }
    }

    private int countOpenRequests(List<EmergencyRequest> requests) {
        return (int) requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING || request.getStatus() == RequestStatus.APPROVED)
                .count();
    }

    private String formatDispatchRate(int dispatchCount, int requestCount) {
        if (requestCount == 0) {
            return "0%";
        }
        int percentage = (int) Math.round((dispatchCount * 100.0) / requestCount);
        return percentage + "%";
    }
}
