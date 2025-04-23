package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.LoginBO;
import org.example.bo.custom.UserBO;
import org.example.dto.UserDto;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditUserProfileController implements Initializable {
    LoginBO loginBO = (LoginBO) BOFactory.getInstance().getBO(BOTypes.LOGIN);
    UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);

    private int selectedUserId = -1; // This should be set when a user is selected for editing
    private String role;

    public AnchorPane editProfileAnchor;
    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void handleResetAction(ActionEvent event) {
        refreshPage();
    }

    private void refreshPage() {
        loadUserdata();
    }

    private void loadUserdata() {
        UserDto user = loginBO.getUser();
        if (user != null) {
            selectedUserId = user.getId();
            role = user.getRole();
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            passwordField.setText("defaultPassword");
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to load data").show();
        }
    }

    @FXML
    void handleUpdateAction(ActionEvent event) {
        //Regex patterns
        String usernamePattern = "^[a-zA-Z0-9._-]{3,20}$";
        String fullnamePattern = "^[A-Za-z ]{3,50}$";
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$";

        // Get values from fields
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String checkedPassword = null;

        if (!"defaultPassword".equals(password)) {
            if (password.matches(passwordPattern)) {
                checkedPassword = password;
            } else {
                showAlert("Invalid Password", "Password must be at least 6 characters and contain letters and numbers.");
                return; // Stop the method if invalid
            }
        }

        // Validate
        if (!username.matches(usernamePattern)) {
            showAlert("Invalid Username", "Username must be 3–20 characters (letters, digits, . _ -).");
            return;
        }

        if (!fullName.matches(fullnamePattern)) {
            showAlert("Invalid Full Name", "Full Name must be 3–50 letters only.");
            return;
        }

        if (!email.matches(emailPattern)) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }

        try {
            if (selectedUserId != -1) {
                UserDto userDto = new UserDto(selectedUserId, username, fullName, email, checkedPassword, role);
                userBO.update(userDto);
                showAlert("Success", "User updated successfully!");
                refreshPage();
            } else {
                showAlert("Error", "No user selected. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Something went wrong while updating the user.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserdata();
    }
}
