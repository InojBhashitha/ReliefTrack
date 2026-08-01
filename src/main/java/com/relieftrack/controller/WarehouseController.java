package com.relieftrack.controller;

import com.relieftrack.model.Warehouse;
import com.relieftrack.service.WarehouseService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class WarehouseController {

    private final WarehouseService warehouseService = new WarehouseService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<Warehouse> warehouseList;

    @FXML
    private TextField nameField;

    @FXML
    private TextField districtField;

    @FXML
    private TextField addressField;

    @FXML
    public void initialize() {
        warehouseList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> populateForm(selected));
        loadWarehouses();
    }

    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseService.findAll();
            warehouseList.getItems().setAll(warehouses);
            summaryLabel.setText("Warehouses registered: " + warehouses.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load warehouse data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddWarehouse() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String district = districtField.getText() == null ? "" : districtField.getText().trim();
        String address = addressField.getText() == null ? "" : addressField.getText().trim();

        if (name.isEmpty() || district.isEmpty() || address.isEmpty()) {
            summaryLabel.setText("Please complete all warehouse form fields.");
            return;
        }

        try {
            warehouseService.save(new Warehouse(0, name, district, address));
            loadWarehouses();
            clearForm();
            summaryLabel.setText("Warehouse added successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to add warehouse. The name may already be in use.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateWarehouse() {
        Warehouse selected = warehouseList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a warehouse to update.");
            return;
        }
        String name = nameField.getText().trim();
        String district = districtField.getText().trim();
        String address = addressField.getText().trim();
        if (name.isEmpty() || district.isEmpty() || address.isEmpty()) {
            summaryLabel.setText("Please complete all warehouse form fields.");
            return;
        }
        try {
            selected.setName(name);
            selected.setDistrict(district);
            selected.setAddress(address);
            warehouseService.update(selected);
            loadWarehouses();
            clearForm();
            summaryLabel.setText("Warehouse updated successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to update warehouse.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteWarehouse() {
        Warehouse selected = warehouseList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a warehouse to delete.");
            return;
        }
        try {
            warehouseService.delete(selected.getWarehouseId());
            loadWarehouses();
            clearForm();
            summaryLabel.setText("Warehouse deleted successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to delete a warehouse that is used by inventory or dispatch records.");
            e.printStackTrace();
        }
    }

    private void populateForm(Warehouse warehouse) {
        if (warehouse == null) return;
        nameField.setText(warehouse.getName());
        districtField.setText(warehouse.getDistrict());
        addressField.setText(warehouse.getAddress());
    }

    private void clearForm() {
        warehouseList.getSelectionModel().clearSelection();
        nameField.clear();
        districtField.clear();
        addressField.clear();
    }
}
