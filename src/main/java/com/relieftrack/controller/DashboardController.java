package com.relieftrack.controller;

import java.io.IOException;

import com.relieftrack.config.AppConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        loadPage("/fxml/home.fxml");
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
        loadPage("/fxml/user.fxml");
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.LOGIN_FXML));
            Scene scene = new Scene(loader.load(), AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);

            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setTitle(AppConfig.APP_TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}