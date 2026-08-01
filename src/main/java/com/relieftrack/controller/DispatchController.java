package com.relieftrack.controller;

import com.relieftrack.model.Dispatch;
import com.relieftrack.service.DispatchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class DispatchController {

    private final DispatchService dispatchService = new DispatchService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<String> dispatchList;

    @FXML
    private TextField requestField;

    @FXML
    private TextField warehouseField;

    @FXML
    private TextField statusField;

    @FXML
    public void initialize() {
        try {
            List<Dispatch> dispatches = dispatchService.findAll();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Dispatch dispatch : dispatches) {
                items.add("Request #" + dispatch.getRequest().getRequestId() + " | " + dispatch.getWarehouse().getName() + " | " + dispatch.getStatus());
            }

            dispatchList.setItems(items);
            summaryLabel.setText("Dispatch records loaded: " + dispatches.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load dispatch data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDispatch() {
        String requestId = requestField.getText() == null ? "" : requestField.getText().trim();
        String warehouse = warehouseField.getText() == null ? "" : warehouseField.getText().trim();
        String status = statusField.getText() == null ? "" : statusField.getText().trim();

        if (requestId.isEmpty() || warehouse.isEmpty() || status.isEmpty()) {
            summaryLabel.setText("Please complete all dispatch form fields.");
            return;
        }

        dispatchList.getItems().add("Request #" + requestId + " | " + warehouse + " | " + status);
        summaryLabel.setText("Dispatch scheduled in the current view.");
        requestField.clear();
        warehouseField.clear();
        statusField.clear();
    }
}
