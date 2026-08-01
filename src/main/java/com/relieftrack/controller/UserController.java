package com.relieftrack.controller;

import com.relieftrack.model.User;
import com.relieftrack.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private Label summaryLabel;

    @FXML
    public void initialize() {
        try {
            List<User> users = userRepository.findAll();
            summaryLabel.setText("Registered users: " + users.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load user data.");
            e.printStackTrace();
        }
    }
}
