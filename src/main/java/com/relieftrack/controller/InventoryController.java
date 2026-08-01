package com.relieftrack.controller;

import com.relieftrack.model.Inventory;
import com.relieftrack.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class InventoryController {

    private final InventoryService inventoryService = new InventoryService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<String> inventoryList;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField warehouseField;

    @FXML
    public void initialize() {
        try {
            List<Inventory> inventories = inventoryService.findAll();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Inventory inventory : inventories) {
                items.add(inventory.getReliefItem().getName() + " | " + inventory.getWarehouse().getName() + " | Qty: " + inventory.getQuantity());
            }

            inventoryList.setItems(items);
            summaryLabel.setText("Inventory records loaded: " + inventories.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load inventory data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddInventory() {
        String itemName = itemNameField.getText() == null ? "" : itemNameField.getText().trim();
        String quantity = quantityField.getText() == null ? "" : quantityField.getText().trim();
        String warehouse = warehouseField.getText() == null ? "" : warehouseField.getText().trim();

        if (itemName.isEmpty() || quantity.isEmpty() || warehouse.isEmpty()) {
            summaryLabel.setText("Please complete all inventory form fields.");
            return;
        }

        inventoryList.getItems().add(itemName + " | " + warehouse + " | Qty: " + quantity);
        summaryLabel.setText("Inventory item added to the current view.");
        itemNameField.clear();
        quantityField.clear();
        warehouseField.clear();
    }
}
