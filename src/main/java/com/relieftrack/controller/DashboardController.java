package com.relieftrack.controller;

import com.relieftrack.config.AppConfig;
import com.relieftrack.config.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label profileLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button inventoryButton;

    @FXML
    private Button warehousesButton;

    @FXML
    private Button requestsButton;

    @FXML
    private Button dispatchButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button usersButton;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        if (profileLabel != null) {
            profileLabel.setText("Welcome, " + SessionManager.getCurrentDisplayName());
        }

        configureNavigation();
        loadPage("/fxml/home.fxml");
    }

    private void configureNavigation() {
        if (usersButton != null) {
            usersButton.setVisible(SessionManager.isAdmin());
            usersButton.setManaged(SessionManager.isAdmin());
        }
    }

    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node page = loader.load();
            contentPane.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        loadPage("/fxml/home.fxml");
    }

    @FXML
    private void showInventory() {
        loadPage("/fxml/inventory.fxml");
    }

    @FXML
    private void showWarehouses() {
        loadPage("/fxml/warehouse.fxml");
    }

    @FXML
    private void showRequests() {
        loadPage("/fxml/request.fxml");
    }

    @FXML
    private void showDispatch() {
        loadPage("/fxml/dispatch.fxml");
    }

    @FXML
    private void showReports() {
        loadPage("/fxml/report.fxml");
    }

    @FXML
    private void showUsers() {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        loadPage("/fxml/user.fxml");
    }

    @FXML
    private void logout() {
        SessionManager.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.LOGIN_FXML));
            Scene scene = new Scene(loader.load(), AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);

            Stage stage = contentPane != null && contentPane.getScene() != null
                    ? (Stage) contentPane.getScene().getWindow()
                    : null;

            if (stage != null) {
                stage.setTitle(AppConfig.APP_TITLE);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access denied");
        alert.setHeaderText("You do not have permission to manage users.");
        alert.setContentText("Please contact an administrator.");
        alert.showAndWait();
    }
}