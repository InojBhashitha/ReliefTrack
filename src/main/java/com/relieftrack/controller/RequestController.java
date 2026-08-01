package com.relieftrack.controller;

import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.service.EmergencyRequestService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class RequestController {

    private final EmergencyRequestService emergencyRequestService = new EmergencyRequestService();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            List<EmergencyRequest> requests = emergencyRequestService.findAll();
            summaryLabel.setText("Open emergency requests: " + requests.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load request data.");
            e.printStackTrace();
        }
    }
}
