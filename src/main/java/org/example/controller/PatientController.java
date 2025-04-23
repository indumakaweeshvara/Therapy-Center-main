package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import org.example.bo.BOFactory;
import org.example.bo.custom.*;
import org.example.dto.PatientDto;
import org.example.dto.TherapyProgramDto;
import org.example.view.tdm.PatientTm;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PatientController implements Initializable {
    PatientBO patientBO  = (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    TherapyProgramBO programBO   = (TherapyProgramBO) BOFactory.getInstance().getBO(BOTypes.PROGRAM);
    LoginBO loginBO   = (LoginBO) BOFactory.getInstance().getBO(BOTypes.LOGIN);
    private ObservableList<PatientTm> patientsList = FXCollections.observableArrayList();
    private int userId;
    @FXML
    private Button clearBtn;

    @FXML
    private ComboBox<String> cmbGender;

    @FXML
    private ComboBox<String> cmbPaymentType;

    @FXML
    private ComboBox<String> cmbProgram;

    @FXML
    private TableColumn<PatientTm, Integer> colAge;

    @FXML
    private TableColumn<PatientTm, String> colEmail;

    @FXML
    private TableColumn<PatientTm, String> colGender;

    @FXML
    private TableColumn<PatientTm, Integer> colId;

    @FXML
    private TableColumn<PatientTm, String> colName;

    @FXML
    private TableColumn<PatientTm, String> colPhone;

    @FXML
    private TableColumn<PatientTm, String> colSessions;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private TableView<PatientTm> tblPatients;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtAge;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtBalance;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPatientId;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtSessions;

    @FXML
    private Button updateBtn;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        setPatientId();
        txtAmount.setDisable(cmbProgram.getSelectionModel().getSelectedItem() == null);
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
        saveBtn.setDisable(false);
        txtAmount.setText("10000.00");
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        PatientTm selectedPatient = tblPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient to delete").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete patient " + selectedPatient.getName() + "?",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean success = patientBO.deletePatient(selectedPatient.getId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully!").show();
                    loadAllPatients();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete patient").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            PatientDto patientDTO = collectPatientData();

            boolean success = patientBO.savePatient(patientDTO);
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Patient saved successfully!").show();
                clearFields();
                loadAllPatients();
                setPatientId();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save patient").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format: " + e.getMessage()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtName.clear();
        cmbGender.getSelectionModel().clearSelection();
        txtAge.clear();
        txtAddress.clear();
        txtPhone.clear();
        txtEmail.clear();
        cmbProgram.getSelectionModel().clearSelection();
        txtSessions.clear();
        cmbPaymentType.getSelectionModel().clearSelection();
        txtAmount.setText("10000.00");
        txtBalance.setText("0.00");
        tblPatients.getSelectionModel().clearSelection();
    }

    private PatientDto collectPatientData() {
        PatientDto patientDTO = new PatientDto();

        // Parse ID (remove 'P' prefix if present)
        String idText = txtPatientId.getText().trim();
        if (idText.startsWith("P")) {
            idText = idText.substring(1);
        }
        patientDTO.setId(Integer.parseInt(idText));

        // Basic patient info
        patientDTO.setName(txtName.getText().trim());
        patientDTO.setGender(cmbGender.getValue());
        patientDTO.setAge(Integer.parseInt(txtAge.getText().trim()));
        patientDTO.setAddress(txtAddress.getText().trim());
        patientDTO.setPhone(txtPhone.getText().trim());
        patientDTO.setEmail(txtEmail.getText().trim());
        patientDTO.setUserId(userId);

        // Get program ID from combobox (format: "ID - Name")
        String programSelection = cmbProgram.getValue();
        String programId = programSelection.split(" - ")[0];
        patientDTO.setProgramId(programId);

        // Session count
        int sessionCount = Integer.parseInt(txtSessions.getText().trim());
        patientDTO.setSessionCount(sessionCount);
        patientDTO.setRemainingSessions(sessionCount);

        // Registration data
        patientDTO.setRegistrationDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        patientDTO.setRegistrationTime(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));

        // Payment data
        patientDTO.setPaymentType(cmbPaymentType.getValue());
        patientDTO.setAmount(new BigDecimal(txtAmount.getText().trim()));
        patientDTO.setBalancePayment(new BigDecimal(txtBalance.getText().trim()));
        patientDTO.setPaymentDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        patientDTO.setPaymentTime(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));

        return patientDTO;
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (txtName.getText().isEmpty()) {
            errors.append("Name cannot be empty\n");
        }

        if (cmbGender.getValue() == null) {
            errors.append("Gender must be selected\n");
        }

        try {
            int age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0 || age > 120) {
                errors.append("Age must be between 1 and 120\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Age must be a valid number\n");
        }

        if (txtAddress.getText().isEmpty()) {
            errors.append("Address cannot be empty\n");
        }

        if (txtPhone.getText().isEmpty()) {
            errors.append("Phone number cannot be empty\n");
        }

        if (cmbProgram.getValue() == null) {
            errors.append("Therapy program must be selected\n");
        }

        try {
            int sessions = Integer.parseInt(txtSessions.getText().trim());
            if (sessions <= 0) {
                errors.append("Number of sessions must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Number of sessions must be a valid number\n");
        }

        if (cmbPaymentType.getValue() == null) {
            errors.append("Payment type must be selected\n");
        }

        try {
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                errors.append("Amount must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Amount must be a valid number\n");
        }

        try {
            BigDecimal balance = new BigDecimal(txtBalance.getText().trim());
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                errors.append("Balance cannot be negative\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Balance must be a valid number\n");
        }

        if (errors.length() > 0) {
            new Alert(Alert.AlertType.ERROR, errors.toString()).show();
            return false;
        }

        return true;
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            PatientDto patientDTO = collectPatientData();

            boolean success = patientBO.updatePatient(patientDTO);
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Patient Updated successfully!").show();
                clearFields();
                loadAllPatients();
                setPatientId();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update patient").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format: " + e.getMessage()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void tblPatientsOnMouseClicked(MouseEvent event) {
        PatientTm selectedPatient = tblPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            try {
                PatientDto patient = patientBO.getPatientById(selectedPatient.getId());
                int Id = patient.getId();
                txtPatientId.setText(String.format("P%03d", Id));
                /*txtPatientId.setText("P" + patient.getId());*/
                txtName.setText(patient.getName());
                cmbGender.setValue(patient.getGender());
                txtAge.setText(String.valueOf(patient.getAge()));
                txtAddress.setText(patient.getAddress());
                txtPhone.setText(patient.getPhone());
                txtEmail.setText(patient.getEmail());
                txtSessions.setText(String.valueOf(patient.getRemainingSessions()));

                cmbPaymentType.setValue(patient.getPaymentType());
                /*txtAmount.setText(String.valueOf(patient.getAmount()));
                txtAmount.setDisable(txtAmount.getText().isEmpty());
                txtBalance.setText(String.valueOf(patient.getBalancePayment()));*/
                caclculateBalance();
                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
                saveBtn.setDisable(true);


                // Set program in the combo box
                String programId = patient.getProgramId();
                String programName = programBO.getProgram(programId).getProgramName();
                cmbProgram.setValue(programId + " - " + programName);


            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error loading patient details: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadAllPatients();
        setupComboBoxes();
        setPatientId();

        // Default payment values
        txtAmount.setText("10000.00");
        txtBalance.setText("0.00");
        userId = loginBO.getUser().getId();

        txtAmount.setDisable(cmbProgram.getSelectionModel().getSelectedItem() == null&&tblPatients.getSelectionModel().getSelectedItem()==null);
        txtAmount.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.matches("\\d+(\\.\\d+)?")) {
                try {
                    caclculateBalance(); // your balance method
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error calculating balance: " + e.getMessage());
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid amount (like 100 or 100.50)").show();}
        });

        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);




    }

    private void setPatientId() {
        try {
            String nextId = patientBO.getNextPatientId();
            txtPatientId.setText(nextId);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate patient ID: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }


    private void setupComboBoxes() {
        // Setup gender combobox
        cmbGender.getItems().addAll("Male", "Female", "Other");

        // Setup payment type combobox
        cmbPaymentType.getItems().addAll("Cash", "Card", "Online Transfer");

        // Setup program combobox
        try {
            List<TherapyProgramDto> programs = programBO.getAll();
            for (TherapyProgramDto program : programs) {
                cmbProgram.getItems().add(program.getProgramId() + " - " + program.getProgramName());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load programs: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAllPatients() {
        try {
            List<PatientTm> patients = patientBO.getAllPatients();
            patientsList.clear();
            patientsList.addAll(patients);
            tblPatients.setItems(patientsList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load patients: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colSessions.setCellValueFactory(new PropertyValueFactory<>("remainingSessions"));
    }

    public void cmbProgramAction(ActionEvent actionEvent) throws SQLException {
        caclculateBalance();
        txtAmount.setDisable(cmbProgram.getSelectionModel().getSelectedItem() == null&&tblPatients.getSelectionModel().getSelectedItem()==null);
    }

    private void caclculateBalance() throws SQLException {
        PatientTm selectedPatient = tblPatients.getSelectionModel().getSelectedItem();

        if (selectedPatient != null) {
            try {
                PatientDto patient = patientBO.getPatientById(selectedPatient.getId());
                cmbPaymentType.setValue(patient.getPaymentType());
                txtAmount.setText(String.valueOf(patient.getAmount()));
                txtBalance.setText(String.valueOf(patient.getBalancePayment()));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error loading patient data: " + e.getMessage()).show();
                e.printStackTrace();
            }
            return;
        }

        String text = txtAmount.getText();

        // Validate amount
        if (!text.matches("\\d+(\\.\\d+)?")) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid amount (like 100 or 100.50)").show();
            return;
        }

        // Validate selected program
        String selectedProgram = cmbProgram.getSelectionModel().getSelectedItem();
        if (selectedProgram == null) {
            txtBalance.setText("0.00"); // During reset or no selection
            return;
        }

        // Calculate balance
        try {
            String programId = selectedProgram.split(" - ")[0];
            TherapyProgramDto program = programBO.getProgram(programId);

            if (program != null) {
                double amount = Double.parseDouble(text);
                double balance = program.getProgramCost().doubleValue() - amount;
                txtBalance.setText(String.format("%.2f", balance));
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to load program details").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }


    public void amountTextChangeAction(InputMethodEvent inputMethodEvent) {
    }
}
