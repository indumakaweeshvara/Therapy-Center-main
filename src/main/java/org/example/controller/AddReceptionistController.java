package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.UserBO;
import org.example.dto.UserDto;
import org.example.entity.Users;
import org.example.view.tdm.UserTM;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddReceptionistController implements Initializable {


    UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);
    private int selectedUserId = -1;
    @FXML
    private TableColumn<UserTM, String> emailColumn;

    @FXML
    private TextField emailField;

    @FXML
    private TableColumn<UserTM, String> fullNameColumn;

    @FXML
    private TextField fullNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TableView<UserTM> receptionistTable;

    @FXML
    private TableColumn<UserTM, String> usernameColumn;

    @FXML
    private TextField usernameField;

    @FXML
    public Button saveBtn;
    @FXML
    public Button updateBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public Button resetBtn;

    @FXML
    void handleDeleteAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> optionalButtonType = alert.showAndWait();

        if (optionalButtonType.isPresent() && optionalButtonType.get() == ButtonType.YES) {
            try {
                if (selectedUserId != -1) {
                    userBO.deleteByPK(String.valueOf(selectedUserId));
                    showAlert("Success", "User deleted successfully!");
                    refreshPage();
                } else {
                    showAlert("Error", "No user selected. Try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }

        }
    }

    @FXML
    void handleResetAction(ActionEvent event) {
        refreshPage();
    }

    @FXML
    void handleSaveAction(ActionEvent event) {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = "Receptionist";

        //Regex patterns
        String usernamePattern = "^[a-zA-Z0-9._-]{3,20}$";
        String fullnamePattern = "^[A-Za-z ]{3,50}$";
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$";

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

        if (!password.matches(passwordPattern)) {
            showAlert("Invalid Password", "Password must be at least 6 characters with letters and numbers.");
            return;
        }

        UserDto userDto = new UserDto(
                0, // ID is auto-generated
                username,
                fullName,
                email,
                password,
                role
        );

        try {
            boolean saved = userBO.save(userDto);
            if (saved) {
                showAlert("Success", "User saved successfully!");
                refreshPage();
            } else {
                showAlert("Error", "User not saved. Try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Exception", "Something went wrong: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
                UserDto userDto = new UserDto(selectedUserId, username, fullName, email, checkedPassword, "Receptionist");
                userBO.update(userDto);
                showAlert("Success", "User updated successfully!");
                refreshPage();
            } else {
                showAlert("Error", "No user selected. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClicktableAction(MouseEvent mouseEvent) {
        UserTM selectedItem = receptionistTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            selectedUserId = selectedItem.getId();
            usernameField.setText(selectedItem.getUsername());
            fullNameField.setText(selectedItem.getFullName());
            emailField.setText(selectedItem.getEmail());
            passwordField.setText("defaultPassword");

            saveBtn.setDisable(true);
            updateBtn.setDisable(false);
            deleteBtn.setDisable(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        try {
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load data").show();
        }
    }

    private void refreshPage() {
        try {
            loadTableData();
            usernameField.clear();
            fullNameField.clear();
            emailField.clear();
            passwordField.clear();

            saveBtn.setDisable(false);
            updateBtn.setDisable(true);
            deleteBtn.setDisable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTableData() throws Exception {
        List<UserDto> allReceptionist = userBO.getAllReceptionist();
        ObservableList<UserTM> userTMS = FXCollections.observableArrayList();

        for (UserDto userDto : allReceptionist) {
            UserTM userTM = new UserTM(
                    userDto.getId(),
                    userDto.getUsername(),
                    userDto.getFullName(),
                    userDto.getEmail(),
                    userDto.getPassword(),
                    userDto.getRole()

            );
            userTMS.add(userTM);
        }
        receptionistTable.setItems(userTMS);


    }

}
