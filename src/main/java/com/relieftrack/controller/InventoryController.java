package com.relieftrack.controller;

import com.relieftrack.model.Inventory;
import com.relieftrack.service.InventoryService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class InventoryController {

    private final InventoryService inventoryService = new InventoryService();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            List<Inventory> inventories = inventoryService.findAll();
            summaryLabel.setText("Inventory records loaded: " + inventories.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load inventory data.");
            e.printStackTrace();
        }
    }
}
