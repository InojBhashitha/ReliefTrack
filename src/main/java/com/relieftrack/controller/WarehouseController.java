package com.relieftrack.controller;

import com.relieftrack.model.Warehouse;
import com.relieftrack.service.WarehouseService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class WarehouseController {

    private final WarehouseService warehouseService = new WarehouseService();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            List<Warehouse> warehouses = warehouseService.findAll();
            summaryLabel.setText("Warehouses registered: " + warehouses.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load warehouse data.");
            e.printStackTrace();
        }
    }
}
