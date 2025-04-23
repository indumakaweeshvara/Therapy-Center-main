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
import org.example.bo.custom.TherapyDetailBO;
import org.example.bo.custom.TherapyProgramBO;
import org.example.dto.TherapistDto;
import org.example.dto.TherapyDetailDto;
import org.example.dto.TherapyProgramDto;
import org.example.view.tdm.TherapyDetailTM;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TherapyDetailController implements Initializable {
    private final TherapyDetailBO therapyDetailBO = (TherapyDetailBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_DETAIL);
    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);
    private final TherapyProgramBO therapyProgramBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.PROGRAM);

    private Integer selectedTherapistId;
    private String selectedProgramId;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<TherapyDetailTM, String> noteColumn;

    @FXML
    private TextArea noteField;

    @FXML
    private TableColumn<TherapyDetailTM, String> programColumn;

    @FXML
    private ComboBox<String> programComboBox;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<TherapyDetailTM, String> therapistColumn;

    @FXML
    private ComboBox<String> therapistComboBox;

    @FXML
    private TableView<TherapyDetailTM> therapyDetailTable;

    @FXML
    private Button updateButton;

    @FXML
    void deleteBtnAction(ActionEvent event) {
        if (selectedTherapistId == null || selectedProgramId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapy detail to delete").show();
            return;
        }

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this therapy detail?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            try {
                boolean isDeleted = therapyDetailBO.deleteByPK(selectedTherapistId, selectedProgramId);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete successful!").show();
                    refreshPage();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete therapy detail").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void onClickedAction(MouseEvent event) {
        TherapyDetailTM selectedDetail = therapyDetailTable.getSelectionModel().getSelectedItem();
        if (selectedDetail != null) {
            // Extract IDs from the display text (format: "ID - Name")
            String therapistText = selectedDetail.getTherapist();
            String programText = selectedDetail.getProgram();

            selectedTherapistId = extractTherapistId(therapistText);
            selectedProgramId = extractProgramId(programText);

            // Set the selected values in combo boxes
            therapistComboBox.setValue(therapistText);
            programComboBox.setValue(programText);

            noteField.setText(selectedDetail.getNote());

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
            // Validate input fields
            if (therapistComboBox.getValue() == null ||
                    programComboBox.getValue() == null ||
                    noteField.getText().isEmpty()) {

                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }

            // Extract IDs from the combo box selections
            int therapistId = extractTherapistId(therapistComboBox.getValue());
            String programId = extractProgramId(programComboBox.getValue());

            // Create TherapyDetailDto from input fields
            TherapyDetailDto detailDto = new TherapyDetailDto(
                    therapistId,
                    getTherapistNameById(therapistId),
                    programId,
                    getProgramNameById(programId),
                    noteField.getText()
            );

            boolean isSaved = therapyDetailBO.save(detailDto);

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Therapy detail saved successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save therapy detail").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private String getProgramNameById(String programId) throws Exception {
        List<TherapyProgramDto> programs = therapyProgramBO.getAll();
        for (TherapyProgramDto program : programs) {
            if (program.getProgramId().equals(programId)) {
                return program.getProgramName();
            }
        }
        return "";
    }

    private String getTherapistNameById(int therapistId) throws Exception {
        List<TherapistDto> therapists = therapistBO.getAll();
        for (TherapistDto therapist : therapists) {
            if (therapist.getId() == therapistId) {
                return therapist.getName();
            }
        }
        return "";
    }

    @FXML
    void updateBtnAction(ActionEvent event) {
        if (selectedTherapistId == null || selectedProgramId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapy detail to update").show();
            return;
        }

        try {
            // Validate input fields
            if (noteField.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill the note field").show();
                return;
            }

            // Create TherapyDetailDto from input fields
            TherapyDetailDto detailDto = new TherapyDetailDto(
                    selectedTherapistId,
                    getTherapistNameById(selectedTherapistId),
                    selectedProgramId,
                    getProgramNameById(selectedProgramId),
                    noteField.getText()
            );

            boolean isUpdated = therapyDetailBO.update(detailDto);

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Therapy detail updated successfully!").show();
                refreshPage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update therapy detail").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        therapistColumn.setCellValueFactory(new PropertyValueFactory<>("therapist"));
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        try {
            loadComboBoxes();
            refreshPage();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load data").show();
        }
    }

    private void refreshPage() {
        try {
            loadTableData();

            therapistComboBox.setValue(null);
            programComboBox.setValue(null);
            noteField.clear();

            selectedTherapistId = null;
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
            List<TherapyDetailDto> details = therapyDetailBO.getAll();

            List<TherapyDetailTM> detailTMList = details.stream()
                    .map(dto -> new TherapyDetailTM(
                            dto.getTherapistId() + " - " + dto.getTherapistName(),
                            dto.getProgramId() + " - " + dto.getProgramName(),
                            dto.getNote()
                    ))
                    .collect(Collectors.toList());

            therapyDetailTable.setItems(FXCollections.observableArrayList(detailTMList));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy detail data").show();
        }
    }


    private void loadComboBoxes() throws Exception {
        // Load therapists
        List<TherapistDto> therapists = therapistBO.getAll();
        List<String> therapistItems = therapists.stream()
                .map(t -> t.getId() + " - " + t.getName())
                .collect(Collectors.toList());
        therapistComboBox.setItems(FXCollections.observableArrayList(therapistItems));

        // Load therapy programs
        List<TherapyProgramDto> programs = therapyProgramBO.getAll();
        List<String> programItems = programs.stream()
                .map(p -> p.getProgramId() + " - " + p.getProgramName())
                .collect(Collectors.toList());
        programComboBox.setItems(FXCollections.observableArrayList(programItems));
    }

    private int extractTherapistId(String therapistText) {
        return Integer.parseInt(therapistText.split(" - ")[0]);
    }

    private String extractProgramId(String programText) {
        return programText.split(" - ")[0];
    }
}
