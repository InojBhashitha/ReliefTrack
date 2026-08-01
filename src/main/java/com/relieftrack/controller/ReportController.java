package com.relieftrack.controller;

import com.relieftrack.service.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class ReportController {

    private final ReportService reportService = new ReportService();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            ReportService.ReportSummary summary = reportService.getSummary();
            summaryLabel.setText(
                    "Inventory: " + summary.getInventoryCount() + " | " +
                    "Warehouses: " + summary.getWarehouseCount() + " | " +
                    "Requests: " + summary.getRequestCount() + " | " +
                    "Dispatches: " + summary.getDispatchCount()
            );
        } catch (SQLException e) {
            summaryLabel.setText("Unable to generate report summary.");
            e.printStackTrace();
        }
    }
}
