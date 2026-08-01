package com.relieftrack.controller;

import com.relieftrack.enums.Category;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.model.Warehouse;
import com.relieftrack.service.InventoryService;
import com.relieftrack.service.ReliefItemService;
import com.relieftrack.service.WarehouseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class InventoryController {

    private final InventoryService inventoryService = new InventoryService();
    private final ReliefItemService reliefItemService = new ReliefItemService();
    private final WarehouseService warehouseService = new WarehouseService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<Inventory> inventoryList;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private ChoiceBox<Warehouse> warehouseChoice;

    @FXML
    private ChoiceBox<Category> categoryChoice;

    @FXML
    private DatePicker expiryDatePicker;

    @FXML
    private TextField minimumStockField;

    @FXML
    public void initialize() {
        categoryChoice.getItems().setAll(Category.values());
        inventoryList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> populateForm(selected));
        loadInventory();
    }

    private void loadInventory() {
        try {
            List<Inventory> inventories = inventoryService.findAll();
            ObservableList<Inventory> items = FXCollections.observableArrayList(inventories);
            inventoryList.setItems(items);
            warehouseChoice.getItems().setAll(warehouseService.findAll());
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
        Warehouse warehouse = warehouseChoice.getValue();
        Category category = categoryChoice.getValue();
        LocalDate expiryDate = expiryDatePicker.getValue();
        String minimumStock = minimumStockField.getText() == null ? "" : minimumStockField.getText().trim();

        if (itemName.isEmpty() || quantity.isEmpty() || minimumStock.isEmpty() || warehouse == null || category == null || expiryDate == null) {
            summaryLabel.setText("Please complete all inventory form fields.");
            return;
        }
        try {
            int parsedQuantity = parseNonNegative(quantity, "Quantity");
            int parsedMinimumStock = parseNonNegative(minimumStock, "Minimum stock");
            ReliefItem reliefItem = findItemByName(itemName);
            if (reliefItem == null) {
                reliefItem = new ReliefItem(0, itemName, category, expiryDate);
                reliefItemService.save(reliefItem);
            }
            inventoryService.save(new Inventory(0, warehouse, reliefItem, parsedQuantity, parsedMinimumStock));
            loadInventory();
            clearForm();
            summaryLabel.setText("Inventory record added successfully.");
        } catch (IllegalArgumentException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to add inventory. This item may already exist at the selected warehouse.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateInventory() {
        Inventory selected = inventoryList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select an inventory record to update.");
            return;
        }
        try {
            String itemName = itemNameField.getText().trim();
            Warehouse warehouse = warehouseChoice.getValue();
            Category category = categoryChoice.getValue();
            LocalDate expiryDate = expiryDatePicker.getValue();
            if (itemName.isEmpty() || warehouse == null || category == null || expiryDate == null) {
                summaryLabel.setText("Please complete all inventory form fields.");
                return;
            }
            ReliefItem item = selected.getReliefItem();
            item.setName(itemName);
            item.setCategory(category);
            item.setExpiryDate(expiryDate);
            reliefItemService.update(item);
            selected.setWarehouse(warehouse);
            selected.setQuantity(parseNonNegative(quantityField.getText().trim(), "Quantity"));
            selected.setMinimumStock(parseNonNegative(minimumStockField.getText().trim(), "Minimum stock"));
            inventoryService.update(selected);
            loadInventory();
            clearForm();
            summaryLabel.setText("Inventory record updated successfully.");
        } catch (IllegalArgumentException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to update inventory.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteInventory() {
        Inventory selected = inventoryList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select an inventory record to delete.");
            return;
        }
        try {
            inventoryService.delete(selected.getInventoryId());
            loadInventory();
            clearForm();
            summaryLabel.setText("Inventory record deleted successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to delete inventory.");
            e.printStackTrace();
        }
    }

    private ReliefItem findItemByName(String name) throws SQLException {
        return reliefItemService.findByName(name).orElse(null);
    }

    private int parseNonNegative(String value, String fieldName) {
        try {
            int number = Integer.parseInt(value);
            if (number < 0) throw new NumberFormatException();
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a whole number of zero or more.");
        }
    }

    private void populateForm(Inventory inventory) {
        if (inventory == null) return;
        itemNameField.setText(inventory.getReliefItem().getName());
        categoryChoice.setValue(inventory.getReliefItem().getCategory());
        expiryDatePicker.setValue(inventory.getReliefItem().getExpiryDate());
        warehouseChoice.setValue(inventory.getWarehouse());
        quantityField.setText(String.valueOf(inventory.getQuantity()));
        minimumStockField.setText(String.valueOf(inventory.getMinimumStock()));
    }

    private void clearForm() {
        inventoryList.getSelectionModel().clearSelection();
        itemNameField.clear();
        categoryChoice.setValue(null);
        expiryDatePicker.setValue(null);
        warehouseChoice.setValue(null);
        quantityField.clear();
        minimumStockField.clear();
    }
}
