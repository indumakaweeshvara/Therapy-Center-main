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
import org.example.bo.custom.TherapyProgramBO;
import org.example.dto.TherapyProgramDto;
import org.example.view.tdm.TherapyProgramTM;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TherapyProgramController implements Initializable {

    TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.PROGRAM);
    private String selectedProgramId;
    @FXML
    public TextField programIDField;
    @FXML
    private Button deleteButton;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TableColumn<TherapyProgramTM, String> descriptionColumn;

    @FXML
    private TableColumn<TherapyProgramTM, String> durationColumn;

    @FXML
    private TextField durationField;

    @FXML
    private TableColumn<TherapyProgramTM, BigDecimal> feeColumn;

    @FXML
    private TextField feeField;

    @FXML
    private TableColumn<TherapyProgramTM, String> idColumn;

    @FXML
    private TableColumn<TherapyProgramTM, String> nameColumn;

    @FXML
    private TextField programNameField;

    @FXML
    private TableView<TherapyProgramTM> programTable;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button updateButton;

    @FXML
    void deleteBtnAction(ActionEvent event) {
        if (selectedProgramId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a program to delete").show();
            return;
        }

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this program?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            try {
                boolean isDeleted = programBO.deleteByPK(selectedProgramId);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete successful!").show();
                    refreshPage();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete program").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void onClickedAction(MouseEvent event) {
        TherapyProgramTM selectedProgram = programTable.getSelectionModel().getSelectedItem();
        if (selectedProgram != null) {
            // Populate fields with selected program data
            selectedProgramId = selectedProgram.getProgramId();
            programIDField.setText(selectedProgram.getProgramId());
            programNameField.setText(selectedProgram.getProgramName());
            durationField.setText(selectedProgram.getDuration());
            feeField.setText(selectedProgram.getProgramCost().toString());
            descriptionArea.setText(selectedProgram.getProgramDescription());

            // Disable program ID field for updates
            programIDField.setDisable(true);

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
            // Get and trim input values
            String programId = programIDField.getText().trim();
            String programName = programNameField.getText().trim();
            String duration = durationField.getText().trim();
            String costText = feeField.getText().trim();
            String description = descriptionArea.getText().trim();

            // Regex patterns
            String idRegex = "^[A-Z]{2}[0-9]{3,6}$";
            String nameRegex = "^[A-Za-z0-9\\s]{3,50}$";
            String durationRegex = "^[0-9]+(\\s?(days|weeks|months))?$"; // e.g., "4 weeks"
            String costRegex = "^\\d+(\\.\\d{2})?$";
            String descriptionRegex = "^.{10,500}$"; // Min 10 characters, max 500

            // Check if any field is empty
            if (programId.isEmpty() || programName.isEmpty() || duration.isEmpty() || costText.isEmpty() || description.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }

            // Validate each field with regex
            if (!programId.matches(idRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Program ID (Use 3-10 uppercase letters/numbers)").show();
                return;
            }

            if (!programName.matches(nameRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Program Name").show();
                return;
            }

            if (!duration.matches(durationRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Duration (e.g., '4 weeks', '30 days')").show();
                return;
            }

            if (!costText.matches(costRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Cost format (e.g., '1000.00')").show();
                return;
            }

            if (!description.matches(descriptionRegex)) {
                new Alert(Alert.AlertType.WARNING, "Description must be 10-500 characters long").show();
                return;
            }

            // Check if program ID already exists
            if (programBO.exists(programId)) {
                new Alert(Alert.AlertType.WARNING, "Program ID already exists. Please use a different ID.").show();
                return;
            }

            // Parse and check cost
            BigDecimal cost = new BigDecimal(costText);
            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                new Alert(Alert.AlertType.WARNING, "Cost must be greater than zero").show();
                return;
            }

            // Create DTO
            TherapyProgramDto programDto = new TherapyProgramDto(
                    programId,
                    programName,
                    duration,
                    cost,
                    description
            );

            boolean isSaved = programBO.save(programDto);

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Program saved successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save program").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }

    }

    @FXML
    void updateBtnAction(ActionEvent event) {
        if (selectedProgramId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a program to update").show();
            return;
        }

        try {
            // Get and trim input values
            String programName = programNameField.getText().trim();
            String duration = durationField.getText().trim();
            String costText = feeField.getText().trim();
            String description = descriptionArea.getText().trim();

            // Regex patterns
            String nameRegex = "^[A-Za-z0-9\\s]{3,50}$";
            String durationRegex = "^[0-9]+(\\s?(days|weeks|months))?$"; // e.g., "4 weeks"
            String costRegex = "^\\d+(\\.\\d{2})?$";
            String descriptionRegex = "^.{10,500}$"; // Min 10 characters, max 500

            // Validate input fields
            if (programName.isEmpty() || duration.isEmpty() || costText.isEmpty() || description.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }

            // Regex validation
            if (!programName.matches(nameRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Program Name (3-50 letters/numbers)").show();
                return;
            }

            if (!duration.matches(durationRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Duration (e.g., '4 weeks', '30 days')").show();
                return;
            }

            if (!costText.matches(costRegex)) {
                new Alert(Alert.AlertType.WARNING, "Invalid Cost format (e.g., '1000.00')").show();
                return;
            }

            if (!description.matches(descriptionRegex)) {
                new Alert(Alert.AlertType.WARNING, "Description must be 10–500 characters").show();
                return;
            }

            // Parse cost
            BigDecimal cost = new BigDecimal(costText);
            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                new Alert(Alert.AlertType.WARNING, "Cost must be greater than zero").show();
                return;
            }

            // Create DTO
            TherapyProgramDto programDto = new TherapyProgramDto(
                    selectedProgramId,
                    programName,
                    duration,
                    cost,
                    description
            );

            boolean isUpdated = programBO.update(programDto);

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Program updated successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update program").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("programId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("programCost"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("programDescription"));

        try {
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy programs").show();
        }
    }

    private void refreshPage() {
        try {
            loadTableData();

            programIDField.clear();
            programNameField.clear();
            durationField.clear();
            feeField.clear();
            descriptionArea.clear();

            programIDField.setDisable(false);
            selectedProgramId = null;
            saveButton.setDisable(false);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTableData() {
        try {
            List<TherapyProgramDto> programs = programBO.getAll();

            List<TherapyProgramTM> programTMList = programs.stream()
                    .map(dto -> new TherapyProgramTM(
                            dto.getProgramId(),
                            dto.getProgramName(),
                            dto.getDuration(),
                            dto.getProgramCost(),
                            dto.getProgramDescription()
                    ))
                    .collect(Collectors.toList());

            programTable.setItems(FXCollections.observableArrayList(programTMList));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load program data").show();
        }
    }
}
