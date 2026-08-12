package com.relieftrack.controller;

import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.Warehouse;
import com.relieftrack.service.EmergencyRequestService;
import com.relieftrack.service.InventoryService;
import com.relieftrack.service.ReportService;
import com.relieftrack.service.WarehouseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class HomeController {

    private final ReportService reportService = new ReportService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final InventoryService inventoryService = new InventoryService();
    private final WarehouseService warehouseService = new WarehouseService();

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
    private VBox warehouseStatusContainer;

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

            // Dynamically load warehouse progress status
            if (warehouseStatusContainer != null) {
                warehouseStatusContainer.getChildren().clear();
                List<Warehouse> warehouses = warehouseService.findAll();
                List<Inventory> allInventory = inventoryService.findAll();

                for (Warehouse warehouse : warehouses) {
                    int totalStock = 0;
                    for (Inventory inv : allInventory) {
                        if (inv.getWarehouse() != null && inv.getWarehouse().getWarehouseId() == warehouse.getWarehouseId()) {
                            totalStock += inv.getQuantity();
                        }
                    }

                    VBox itemBox = new VBox(6);
                    double progress = Math.min(1.0, totalStock / 200.0);
                    Label nameLabel = new Label(warehouse.getName() + " (" + totalStock + " items)");
                    nameLabel.getStyleClass().add("mini-label");

                    ProgressBar progressBar = new ProgressBar(progress);
                    progressBar.setMaxWidth(Double.MAX_VALUE);

                    itemBox.getChildren().addAll(nameLabel, progressBar);
                    warehouseStatusContainer.getChildren().add(itemBox);
                }
            }

        } catch (SQLException e) {
            inventoryCountLabel.setText("-");
            openRequestCountLabel.setText("-");
            warehouseCountLabel.setText("-");
            dispatchRateLabel.setText("-");
            requestTable.setItems(FXCollections.emptyObservableList());
            alertsList.setItems(FXCollections.observableArrayList("Unable to load dashboard data."));
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuickInventory() {
        DashboardController dashboard = DashboardController.getInstance();
        if (dashboard != null) {
            dashboard.showInventory();
        }
    }

    @FXML
    private void handleQuickDispatches() {
        DashboardController dashboard = DashboardController.getInstance();
        if (dashboard != null) {
            dashboard.showDispatch();
        }
    }

    @FXML
    private void handleQuickReports() {
        DashboardController dashboard = DashboardController.getInstance();
        if (dashboard != null) {
            dashboard.showReports();
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
