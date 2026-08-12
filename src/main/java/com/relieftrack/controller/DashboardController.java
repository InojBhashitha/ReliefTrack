package com.relieftrack.controller;

import java.io.IOException;

import com.relieftrack.config.AppConfig;
import com.relieftrack.config.SessionManager;
import com.relieftrack.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController {

    private static DashboardController instance;

    public static DashboardController getInstance() {
        return instance;
    }

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
        instance = this;
        if (!SessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        if (profileLabel != null) {
            profileLabel.setText("Welcome, " + SessionManager.getCurrentDisplayName());
        }

        configureNavigation();
        loadPage("/fxml/home.fxml");
        updateActiveButton(dashboardButton);
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

    private void updateActiveButton(Button activeButton) {
        java.util.List<Button> buttons = java.util.List.of(
                dashboardButton, inventoryButton, warehousesButton,
                requestsButton, dispatchButton, reportsButton, usersButton
        );
        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
                if (!button.getStyleClass().contains("nav-button")) {
                    button.getStyleClass().add("nav-button");
                }
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    public void showDashboard() {
        loadPage("/fxml/home.fxml");
        updateActiveButton(dashboardButton);
    }

    @FXML
    public void showInventory() {
        loadPage("/fxml/inventory.fxml");
        updateActiveButton(inventoryButton);
    }

    @FXML
    public void showWarehouses() {
        loadPage("/fxml/warehouse.fxml");
        updateActiveButton(warehousesButton);
    }

    @FXML
    public void showRequests() {
        loadPage("/fxml/request.fxml");
        updateActiveButton(requestsButton);
    }

    @FXML
    public void showDispatch() {
        loadPage("/fxml/dispatch.fxml");
        updateActiveButton(dispatchButton);
    }

    @FXML
    public void showReports() {
        loadPage("/fxml/report.fxml");
        updateActiveButton(reportsButton);
    }

    @FXML
    public void showUsers() {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        loadPage("/fxml/user.fxml");
        updateActiveButton(usersButton);
    }

    @FXML
    private void logout() {
        AuthenticationService.clearSession();
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