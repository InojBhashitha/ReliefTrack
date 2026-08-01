package com.relieftrack.controller;

import com.relieftrack.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;

public class ReportController {

    private final ReportService reportService = new ReportService();

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
    private ListView<String> reportList;

    @FXML
    public void initialize() {
        try {
            ReportService.ReportSummary summary = reportService.getSummary();

            inventoryStat.setText(String.valueOf(summary.getInventoryCount()));
            warehouseStat.setText(String.valueOf(summary.getWarehouseCount()));
            requestStat.setText(String.valueOf(summary.getRequestCount()));
            dispatchStat.setText(String.valueOf(summary.getDispatchCount()));

            summaryLabel.setText("Operational summary generated successfully.");

            ObservableList<String> items = FXCollections.observableArrayList(
                    "Inventory items tracked: " + summary.getInventoryCount(),
                    "Warehouses operational: " + summary.getWarehouseCount(),
                    "Emergency requests in system: " + summary.getRequestCount(),
                    "Dispatch records available: " + summary.getDispatchCount()
            );

            reportList.setItems(items);
        } catch (SQLException e) {
            summaryLabel.setText("Unable to generate report summary.");
            e.printStackTrace();
        }
    }
}
