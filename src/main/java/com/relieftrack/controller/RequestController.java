package com.relieftrack.controller;

import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.service.EmergencyRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class RequestController {

    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<String> requestList;

    @FXML
    private TextField organizationField;

    @FXML
    private TextField priorityField;

    @FXML
    private TextField itemField;

    @FXML
    public void initialize() {
        try {
            List<EmergencyRequest> requests = emergencyRequestService.findAll();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (EmergencyRequest request : requests) {
                items.add(request.getOrganization() + " | " + request.getPriority() + " | " + request.getReliefItem().getName());
            }

            requestList.setItems(items);
            summaryLabel.setText("Open emergency requests: " + requests.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load request data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddRequest() {
        String organization = organizationField.getText() == null ? "" : organizationField.getText().trim();
        String priority = priorityField.getText() == null ? "" : priorityField.getText().trim();
        String item = itemField.getText() == null ? "" : itemField.getText().trim();

        if (organization.isEmpty() || priority.isEmpty() || item.isEmpty()) {
            summaryLabel.setText("Please complete all request form fields.");
            return;
        }

        requestList.getItems().add(organization + " | " + priority + " | " + item);
        summaryLabel.setText("Request added to the current view.");
        organizationField.clear();
        priorityField.clear();
        itemField.clear();
    }
}
