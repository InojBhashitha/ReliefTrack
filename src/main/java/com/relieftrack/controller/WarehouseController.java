package com.relieftrack.controller;

import com.relieftrack.model.Warehouse;
import com.relieftrack.service.WarehouseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ListView<String> warehouseList;

    @FXML
    private TextField nameField;

    @FXML
    private TextField districtField;

    @FXML
    private TextField addressField;

    @FXML
    public void initialize() {
        try {
            List<Warehouse> warehouses = warehouseService.findAll();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Warehouse warehouse : warehouses) {
                items.add(warehouse.getName() + " | " + warehouse.getDistrict() + " | " + warehouse.getAddress());
            }

            warehouseList.setItems(items);
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

        warehouseList.getItems().add(name + " | " + district + " | " + address);
        summaryLabel.setText("Warehouse added to the current view.");
        nameField.clear();
        districtField.clear();
        addressField.clear();
    }
}
