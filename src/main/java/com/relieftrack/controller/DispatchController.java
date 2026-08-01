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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceBox;

import java.sql.SQLException;
import java.util.List;

public class DispatchController {

    private final DispatchService dispatchService = new DispatchService();
    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();
    private final WarehouseService warehouseService = new WarehouseService();

    @FXML
    private Label summaryLabel;

    @FXML
    private ListView<Dispatch> dispatchList;

    @FXML
    private ChoiceBox<EmergencyRequest> requestChoice;

    @FXML
    private ChoiceBox<Warehouse> warehouseChoice;

    @FXML
    private ChoiceBox<DispatchStatus> statusChoice;

    @FXML
    public void initialize() {
        statusChoice.getItems().setAll(DispatchStatus.values());
        dispatchList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> populateForm(selected));
        loadDispatches();
    }

    private void loadDispatches() {
        try {
            List<Dispatch> dispatches = dispatchService.findAll();
            ObservableList<Dispatch> items = FXCollections.observableArrayList(dispatches);
            dispatchList.setItems(items);
            requestChoice.getItems().setAll(emergencyRequestService.findAll().stream()
                    .filter(request -> request.getStatus() == com.relieftrack.enums.RequestStatus.PENDING
                            || request.getStatus() == com.relieftrack.enums.RequestStatus.APPROVED)
                    .toList());
            warehouseChoice.getItems().setAll(warehouseService.findAll());
            summaryLabel.setText("Dispatch records loaded: " + dispatches.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load dispatch data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDispatch() {
        try {
            dispatchService.scheduleDispatch(requestChoice.getValue(), warehouseChoice.getValue());
            loadDispatches();
            clearForm();
            summaryLabel.setText("Dispatch scheduled and inventory reserved successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            summaryLabel.setText(e.getMessage());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to schedule dispatch. No changes were saved.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Dispatch selected = dispatchList.getSelectionModel().getSelectedItem();
        DispatchStatus status = statusChoice.getValue();
        if (selected == null || status == null) {
            summaryLabel.setText("Select a dispatch and a status to update.");
            return;
        }
        try {
            selected.setStatus(status);
            dispatchService.update(selected);
            loadDispatches();
            clearForm();
            summaryLabel.setText("Dispatch status updated successfully.");
        } catch (SQLException e) {
            summaryLabel.setText("Unable to update dispatch status.");
            e.printStackTrace();
        }
    }

    private void populateForm(Dispatch dispatch) {
        if (dispatch == null) return;
        statusChoice.setValue(dispatch.getStatus());
    }

    private void clearForm() {
        dispatchList.getSelectionModel().clearSelection();
        requestChoice.setValue(null);
        warehouseChoice.setValue(null);
        statusChoice.setValue(DispatchStatus.PENDING);
    }
}
