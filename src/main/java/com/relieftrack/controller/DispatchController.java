package com.relieftrack.controller;

import com.relieftrack.enums.DispatchStatus;
import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Warehouse;
import com.relieftrack.service.DispatchService;
import com.relieftrack.service.EmergencyRequestService;
import com.relieftrack.service.WarehouseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DispatchController {

    private final DispatchService dispatchService = new DispatchService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final WarehouseService warehouseService = new WarehouseService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<String> dispatchList;

    @FXML
    private ChoiceBox<EmergencyRequest> requestChoice;

    @FXML
    private ChoiceBox<Warehouse> warehouseChoice;

    @FXML
    private ChoiceBox<DispatchStatus> statusChoice;

    @FXML
    public void initialize() {
        statusChoice.getItems().setAll(DispatchStatus.values());
        statusChoice.setValue(DispatchStatus.PENDING);
        dispatchList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> {
            if (selected != null) {
                try {
                    int id = extractDispatchId(selected);
                    Dispatch dispatch = dispatchService.findById(id);
                    if (dispatch != null) {
                        statusChoice.setValue(dispatch.getStatus());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loadDispatches();
    }

    private void loadDispatches() {
        try {
            List<Dispatch> dispatches = dispatchService.findAll();

            ObservableList<String> items = FXCollections.observableArrayList(
                    dispatches.stream()
                            .map(this::formatDispatch)                .collect(Collectors.toList())
            );
            dispatchList.setItems(items);

            // Load requests using Priority Queue (highest priority first)
                            
            List<EmergencyRequest> schedulableRequests =
                    emergencyRequestService.prioritizeSchedulableRequests(
                            emergencyRequestService.findAll());

            requestChoice.getItems().setAll(schedulableRequests);
                    

            warehouseChoice.getItems().setAll(warehouseService.findAll());

            summaryLabel.setText(
                    "Dispatch records loaded: "
                            + dispatches.size()
                            + " | Pending/Approved requests: "
                            + schedulableRequests.size());

        } catch (SQLException e) {
            summaryLabel.setText("Unable to load dispatch data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDispatch() {
        try {
            dispatchService.scheduleDispatch(
                    requestChoice.getValue(),
                    warehouseChoice.getValue());

            loadDispatches();
            clearForm();

            summaryLabel.setText(
                    "Dispatch scheduled and inventory reserved successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText(
                    "Unable to schedule dispatch. No changes were saved.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStatus() {
        String selected = dispatchList.getSelectionModel().getSelectedItem();
        DispatchStatus status = statusChoice.getValue();

        if (selected == null || status == null) {
            summaryLabel.setText(
                    "Select a dispatch and a status to update.");
            return;
        }

        try {
            int dispatchId = extractDispatchId(selected);

            Dispatch selectedDispatch =
                    dispatchService.findById(dispatchId);

            if (selectedDispatch == null) {
                summaryLabel.setText(
                        "Selected dispatch could not be found.");
                return;
            }

            selectedDispatch.setStatus(status);
                
            dispatchService.update(selectedDispatch);

            loadDispatches();
            clearForm();

            summaryLabel.setText(
                    "Dispatch status updated successfully.");

        } catch (SQLException e) {
            summaryLabel.setText(
                    "Unable to update dispatch status.");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        dispatchList.getSelectionModel().clearSelection();
        requestChoice.setValue(null);
        warehouseChoice.setValue(null);
        statusChoice.setValue(DispatchStatus.PENDING);
    }

    private String formatDispatch(Dispatch dispatch) {

        String requestLabel =
                dispatch.getRequest() != null
                        ? dispatch.getRequest().getOrganization()
                        : "Unknown request";

        String warehouseLabel =
                dispatch.getWarehouse() != null
                        ? dispatch.getWarehouse().getName()
                        : "Unknown warehouse";

        return "#"
                + dispatch.getDispatchId()
                + " | "
                + requestLabel
                + " | "
                + warehouseLabel
                + " | "
                + dispatch.getStatus();
    }

    private int extractDispatchId(String formattedValue) {
        String trimmed =
                formattedValue.substring(
                        1,
                        formattedValue.indexOf(' '));

        return Integer.parseInt(trimmed);
    }
}