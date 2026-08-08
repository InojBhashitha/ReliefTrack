package com.relieftrack.controller;

import com.relieftrack.config.AppConfig;
import com.relieftrack.config.SessionManager;
import com.relieftrack.model.User;
import com.relieftrack.service.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    private final AuthenticationService authenticationService = new AuthenticationService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            Optional<User> user = authenticationService.authenticate(username, password);

            if (user.isPresent()) {
                AuthenticationService.setCurrentUser(user.get());
                SessionManager.login(user.get());
                statusLabel.setText("Login successful. Opening dashboard...");
                openDashboard(event);
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Authentication failed due to a database error.");
            e.printStackTrace();
        }
    }

    private void openDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.DASHBOARD_FXML));
            Scene scene = new Scene(loader.load(), AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(AppConfig.APP_TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Unable to open the dashboard.");
            e.printStackTrace();
        }
    }
}
