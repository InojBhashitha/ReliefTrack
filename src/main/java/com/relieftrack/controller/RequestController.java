package com.relieftrack.controller;

import com.relieftrack.enums.DisasterType;
import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.service.EmergencyRequestService;
import com.relieftrack.service.ReliefItemService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RequestController {

    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final ReliefItemService reliefItemService = new ReliefItemService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<EmergencyRequest> requestList;

    @FXML
    private TextField organizationField;

    @FXML
    private ChoiceBox<PriorityLevel> priorityChoice;

    @FXML
    private ChoiceBox<ReliefItem> itemChoice;

    @FXML
    private ChoiceBox<DisasterType> disasterTypeChoice;

    @FXML
    private ChoiceBox<RequestStatus> statusChoice;

    @FXML
    private TextField quantityField;

    @FXML
    public void initialize() {
        disasterTypeChoice.getItems().setAll(DisasterType.values());
        priorityChoice.getItems().setAll(PriorityLevel.values());
        statusChoice.getItems().setAll(RequestStatus.values());
        statusChoice.setValue(RequestStatus.PENDING);
        requestList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> populateForm(selected));
        loadRequests();
    }

    private void loadRequests() {
        try {
            List<EmergencyRequest> requests = emergencyRequestService.findAll();
            ObservableList<EmergencyRequest> items = FXCollections.observableArrayList(requests);
            requestList.setItems(items);
            itemChoice.getItems().setAll(reliefItemService.findAll());
            int pending = emergencyRequestService.prioritizePendingRequests(requests).size();
            summaryLabel.setText("Emergency requests: " + requests.size() + " | Pending triage queue: " + pending);
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load request data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddRequest() {
        String organization = organizationField.getText() == null ? "" : organizationField.getText().trim();
        ReliefItem item = itemChoice.getValue();
        DisasterType disasterType = disasterTypeChoice.getValue();
        PriorityLevel priority = priorityChoice.getValue();
        String quantity = quantityField.getText() == null ? "" : quantityField.getText().trim();

        if (organization.isEmpty() || item == null || disasterType == null || priority == null || quantity.isEmpty()) {
            summaryLabel.setText("Please complete all request form fields.");
            return;
        }
        try {
            EmergencyRequest request = new EmergencyRequest(0, organization, disasterType, item,
                    parsePositiveQuantity(quantity), priority, RequestStatus.PENDING, LocalDateTime.now());
            emergencyRequestService.save(request);
            loadRequests();
            clearForm();
            summaryLabel.setText("Request added to the pending triage queue.");
        } catch (IllegalArgumentException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to add request.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateRequest() {
        EmergencyRequest selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a request to update.");
            return;
        }
        String organization = organizationField.getText().trim();
        ReliefItem item = itemChoice.getValue();
        DisasterType disasterType = disasterTypeChoice.getValue();
        PriorityLevel priority = priorityChoice.getValue();
        RequestStatus status = statusChoice.getValue();
        if (organization.isEmpty() || item == null || disasterType == null || priority == null || status == null) {
            summaryLabel.setText("Please complete all request form fields.");
            return;
        }
        try {
            selected.setOrganization(organization);
            selected.setReliefItem(item);
            selected.setDisasterType(disasterType);
            selected.setQuantity(parsePositiveQuantity(quantityField.getText().trim()));
            selected.setPriority(priority);
            selected.setStatus(status);
            emergencyRequestService.update(selected);
            loadRequests();
            clearForm();
            summaryLabel.setText("Request updated successfully.");
        } catch (IllegalArgumentException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to update request.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteRequest() {
        EmergencyRequest selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a request to delete.");
            return;
        }
        try {
            emergencyRequestService.delete(selected.getRequestId());
            loadRequests();
            clearForm();
            summaryLabel.setText("Request deleted successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to delete a request that has dispatch records.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowTriageQueue() {
        try {
            List<EmergencyRequest> prioritized = emergencyRequestService.prioritizePendingRequests(emergencyRequestService.findAll());
            requestList.setItems(FXCollections.observableArrayList(prioritized));
            summaryLabel.setText("Pending triage queue: highest priority first (" + prioritized.size() + ").");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load the triage queue.");
            e.printStackTrace();
        }
    }

    private int parsePositiveQuantity(String value) {
        try {
            int quantity = Integer.parseInt(value);
            if (quantity <= 0) throw new NumberFormatException();
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a whole number greater than zero.");
        }
    }

    private void populateForm(EmergencyRequest request) {
        if (request == null) return;
        organizationField.setText(request.getOrganization());
        itemChoice.getItems().stream()
                .filter(item -> item.getItemId() == request.getReliefItem().getItemId())
                .findFirst()
                .ifPresent(itemChoice::setValue);
        disasterTypeChoice.setValue(request.getDisasterType());
        priorityChoice.setValue(request.getPriority());
        statusChoice.setValue(request.getStatus());
        quantityField.setText(String.valueOf(request.getQuantity()));
    }

    private void clearForm() {
        requestList.getSelectionModel().clearSelection();
        organizationField.clear();
        itemChoice.setValue(null);
        disasterTypeChoice.setValue(null);
        priorityChoice.setValue(null);
        statusChoice.setValue(RequestStatus.PENDING);
        quantityField.clear();
    }
}
