package com.relieftrack.controller;

import com.relieftrack.enums.Role;
import com.relieftrack.model.User;
import com.relieftrack.service.AuthenticationService;
import com.relieftrack.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserService userService = new UserService();
    private final AuthenticationService authenticationService = new AuthenticationService();

    @FXML
    private Label summaryLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<Role> roleChoice;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> fullNameColumn;

    @FXML
    private TableColumn<User, Role> roleColumn;

    @FXML
    public void initialize() {
        roleChoice.getItems().setAll(Role.values());
        roleChoice.setValue(Role.ADMIN);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();
    }

    @FXML
    private void handleAddUser() {
        try {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setFullName(fullNameField.getText());
            user.setRole(roleChoice.getValue());
            user.setPasswordHash(authenticationService.hashPassword(passwordField.getText()));

            userService.save(user);
            loadUsers();
            summaryLabel.setText("User added successfully.");
            clearForm();
        } catch (SQLException e) {
            summaryLabel.setText("Unable to add user.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a user to update.");
            return;
        }

        try {
            selected.setUsername(usernameField.getText());
            selected.setFullName(fullNameField.getText());
            selected.setRole(roleChoice.getValue());
            selected.setPasswordHash(authenticationService.hashPassword(passwordField.getText()));

            userService.update(selected);
            loadUsers();
            summaryLabel.setText("User updated successfully.");
            clearForm();
        } catch (SQLException e) {
            summaryLabel.setText("Unable to update user.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            summaryLabel.setText("Select a user to delete.");
            return;
        }

        try {
            userService.delete(selected.getUserId());
            loadUsers();
            summaryLabel.setText("User deleted successfully.");
            clearForm();
        } catch (SQLException e) {
            summaryLabel.setText("Unable to delete user.");
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            List<User> users = userService.findAll();
            ObservableList<User> items = FXCollections.observableArrayList(users);
            userTable.setItems(items);
            summaryLabel.setText("User records loaded: " + users.size());
        } catch (SQLException e) {
            summaryLabel.setText("Unable to load user data.");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        usernameField.clear();
        fullNameField.clear();
        passwordField.clear();
        roleChoice.setValue(Role.ADMIN);
    }
}
