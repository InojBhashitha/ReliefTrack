package com.relieftrack.controller;

import com.relieftrack.model.Dispatch;
import com.relieftrack.service.DispatchService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class DispatchController {

    private final DispatchService dispatchService = new DispatchService();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            List<Dispatch> dispatches = dispatchService.findAll();
            summaryLabel.setText("Dispatch records loaded: " + dispatches.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load dispatch data.");
            e.printStackTrace();
        }
    }
}
