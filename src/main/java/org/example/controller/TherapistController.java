package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.TherapistBO;
import org.example.dto.TherapistDto;
import org.example.view.tdm.TherapistTM;


import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TherapistController implements Initializable {
    TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);
    private Integer selectedTherapistId;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<TherapistTM, String> emailColumn;

    @FXML
    private TextField emailField;

    @FXML
    private TableColumn<TherapistTM, String> idColumn;

    @FXML
    private TableColumn<TherapistTM, String> nameColumn;

    @FXML
    private TableColumn<TherapistTM, String> phoneColumn;

    @FXML
    private TextField phoneField;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<TherapistTM, String> specializationColumn;

    @FXML
    private TextField specializationField;

    @FXML
    private TextField therapistNameField;

    @FXML
    private TableView<TherapistTM> therapistTable;

    @FXML
    private Button updateButton;

    @FXML
    void deleteBtnAction(ActionEvent event) {
        if (selectedTherapistId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to delete").show();
            return;
        }

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this therapist?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            try {
                boolean isDeleted = therapistBO.deleteByPK(String.valueOf(selectedTherapistId));
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete successful!").show();
                    refreshPage();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete therapist").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void onClickedAction(MouseEvent event) {
        TherapistTM selectedTherapist = therapistTable.getSelectionModel().getSelectedItem();
        if (selectedTherapist != null) {
            // Populate fields with selected therapist data
            selectedTherapistId = selectedTherapist.getId();
            therapistNameField.setText(selectedTherapist.getName());
            emailField.setText(selectedTherapist.getEmail());
            phoneField.setText(selectedTherapist.getPhone());
            specializationField.setText(selectedTherapist.getSpecialization());

            // Enable update and delete buttons
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            saveButton.setDisable(true);
        }
    }

    @FXML
    void resetBtnAction(ActionEvent event) {
        refreshPage();
    }

    @FXML
    void saveBtnAction(ActionEvent event) {
        try {
            // Validate input fields using regex
            String name = therapistNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String specialization = specializationField.getText().trim();

            // Regex patterns
            String nameRegex = "^[A-Za-z\\s]{2,50}$";
            String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$";
            String phoneRegex = "^[0-9]{10}$"; // Adjust for your phone format
            String specializationRegex = "^[A-Za-z\\s]{2,50}$";

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || specialization.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }

            if (!name.matches(nameRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid therapist name!").show();
                return;
            }

            if (!email.matches(emailRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid email address!").show();
                return;
            }

            if (!phone.matches(phoneRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid phone number!").show();
                return;
            }

            if (!specialization.matches(specializationRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid specialization!").show();
                return;
            }

            // Create TherapistDto from input fields
            TherapistDto therapistDto = new TherapistDto(
                    0, // ID will be auto-generated
                    name,
                    email,
                    phone,
                    specialization
            );

            boolean isSaved = therapistBO.save(therapistDto);

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Therapist saved successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save therapist").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }

    }

    @FXML
    void updateBtnAction(ActionEvent event) {
        if (selectedTherapistId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to update").show();
            return;
        }

        try {
            // Get input values
            String name = therapistNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String specialization = specializationField.getText().trim();

            // Validate empty fields
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || specialization.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }

            // Regex validations
            if (!name.matches("^[A-Za-z\\s]{3,50}$")) {
                new Alert(Alert.AlertType.WARNING, "Invalid name (only letters & spaces, 3-50 characters)").show();
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                new Alert(Alert.AlertType.WARNING, "Invalid email format").show();
                return;
            }

            if (!phone.matches("^\\d{10}$")) { // Modify this for different formats if needed
                new Alert(Alert.AlertType.WARNING, "Invalid phone number (must be 10 digits)").show();
                return;
            }

            if (!specialization.matches("^[A-Za-z\\s,/]{3,100}$")) {
                new Alert(Alert.AlertType.WARNING, "Invalid specialization (only letters, spaces, commas or slashes)").show();
                return;
            }

            // Create DTO and update
            TherapistDto therapistDto = new TherapistDto(
                    selectedTherapistId,
                    name,
                    email,
                    phone,
                    specialization
            );

            boolean isUpdated = therapistBO.update(therapistDto);

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Therapist updated successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update therapist").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));

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
            therapistNameField.clear();
            phoneField.clear();
            emailField.clear();
            specializationField.clear();

            selectedTherapistId = null;
            saveButton.setDisable(false);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTableData() {
        try {
            List<TherapistDto> therapists = therapistBO.getAll();

            List<TherapistTM> therapistTMList = therapists.stream()
                    .map(dto -> new TherapistTM(
                            dto.getId(),
                            dto.getName(),
                            dto.getEmail(),
                            dto.getPhone(),
                            dto.getSpecialization()
                    ))
                    .collect(Collectors.toList());

            therapistTable.setItems(FXCollections.observableArrayList(therapistTMList));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load therapist data").show();
        }
    }
}
