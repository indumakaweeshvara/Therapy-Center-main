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
import org.example.bo.custom.*;
import org.example.dto.PaymentDto;
import org.example.dto.TherapistDto;
import org.example.dto.TherapySessionDto;
import org.example.entity.TherapySessionId;
import org.example.view.tdm.TherapySessionTm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SessionController implements Initializable {

    TherapySessionBO therapySessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOTypes.THERAPY_SESSION);
    PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOTypes.PATIENT);
    TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOTypes.THERAPIST);
    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOTypes.PAYMENT);

    private ObservableList<TherapySessionTm> sessionsList = FXCollections.observableArrayList();
    private BigDecimal minimumPaymentAmount ;

    @FXML
    private Button clearBtn;

    @FXML
    public Label minAmountLabel;

    @FXML
    private ComboBox<String> cmbPatient;

    @FXML
    private ComboBox<String> cmbStatus;

    @FXML
    private ComboBox<String> cmbTherapist;

    @FXML
    private ComboBox<String> cmbTime;

    @FXML
    private TableColumn<TherapySessionTm, String> colDate;

    @FXML
    private TableColumn<TherapySessionTm, Integer> colPatientId;

    @FXML
    private TableColumn<TherapySessionTm, String> colPatientName;

    @FXML
    private TableColumn<TherapySessionTm, String> colStatus;

    @FXML
    private TableColumn<TherapySessionTm, Integer> colTherapistId;

    @FXML
    private TableColumn<TherapySessionTm, String> colTherapistName;

    @FXML
    private TableColumn<TherapySessionTm, String> colTime;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button saveBtn;

    @FXML
    private TableView<TherapySessionTm> tblSessions;

    @FXML
    private TextField txtPaymentAmount;

    @FXML
    private TextArea txtSessionNotes;

    @FXML
    private Button updateBtn;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        updateBtn.setDisable(true);
        /*deleteBtn.setDisable(true)*/;
        saveBtn.setDisable(false);
    }

    private void clearFields() {
        cmbPatient.getSelectionModel().clearSelection();
        cmbTherapist.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        cmbTime.getSelectionModel().clearSelection();
        cmbStatus.getSelectionModel().clearSelection();
        txtSessionNotes.clear();
        txtPaymentAmount.clear();
        tblSessions.getSelectionModel().clearSelection();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            TherapySessionDto sessionDTO = collectSessionData();
            if (sessionDTO == null) {
                return; // stop further execution if sessionDTO is null
            }
            double paymentAmount = 0.0;

            if (!txtPaymentAmount.getText().isEmpty()) {
                paymentAmount = Double.parseDouble(txtPaymentAmount.getText().trim());
            }

            boolean success = therapySessionBO.scheduleSession(sessionDTO, paymentAmount);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Session scheduled successfully!").show();
                clearFields();
                loadAllSessions();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to schedule session. Check if therapist is available or patient has remaining sessions.").show();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format: " + e.getMessage()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        calculateMinimumPayment();
        if (!validateInputs()) {
            return;
        }
        try{
            TherapySessionDto sessionDTO = collectSessionDataUpdate();
            if (sessionDTO == null) {
                return; // stop further execution if sessionDTO is null
            }
            TherapySessionId id=new TherapySessionId(
                    sessionDTO.getTherapistId(),
                    sessionDTO.getPatientId()
            );

            double paymentAmount = 0.0;

            if (!txtPaymentAmount.getText().isEmpty()) {
                paymentAmount = Double.parseDouble(txtPaymentAmount.getText().trim());
            }

            if (paymentAmount < 0||paymentAmount<Double.parseDouble(String.valueOf(minimumPaymentAmount))) {
                new Alert(Alert.AlertType.ERROR, "Payment amount must be greater than or equal to " + minimumPaymentAmount).show();
                return;
            }

            boolean success = therapySessionBO.ReScheduleSession(sessionDTO, paymentAmount,id);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Session Updated successfully!").show();
                clearFields();
                loadAllSessions();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Update session. Check if therapist is available or patient has remaining sessions.").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            throw new RuntimeException(e);

        }
    }

    @FXML
    void checkTherapistAvailability(ActionEvent event) {

    }

    @FXML
    void tblSessionsOnMouseClicked(MouseEvent event) {
        updateBtn.setDisable(false);
        /*deleteBtn.setDisable(false);*/
        saveBtn.setDisable(true);
        TherapySessionTm selectedSession = tblSessions.getSelectionModel().getSelectedItem();
        if (selectedSession != null) {
                    // Set patient in combobox
                    cmbPatient.setValue(selectedSession.getPatientId() + " - " + selectedSession.getPatientName());

                    // Set therapist in combobox
                    cmbTherapist.setValue(selectedSession.getTherapistId() + " - " + selectedSession.getTherapistName());

                    // Set date
                    datePicker.setValue(LocalDate.parse(selectedSession.getDate()));

                    // Set time and status
                    cmbTime.setValue(selectedSession.getTime());
                    cmbStatus.setValue(selectedSession.getStatus());
                    txtSessionNotes.setText(selectedSession.getSessionNote());

            BigDecimal paymentAmount = paymentBO.findPaymentAmount(selectedSession.getPatientId(),selectedSession.getDate(), selectedSession.getTime());
            if (paymentAmount != null) {
                txtPaymentAmount.setText(String.valueOf(paymentAmount));
            } else {
                txtPaymentAmount.setText("0.00");
            }

                   // Set payment amount
                   /* txtPaymentAmount.setText(String.valueOf(paymentAmount));*/

                   // Set therapist in combobox
                   /* cmbTherapist.setValue(selectedSession.getTherapistId() + " - " + selectedSession.getTherapistName());*/


            // Set notes
                   /* txtSessionNotes.setText(selectedSession.get);

                    // Clear payment amount as it's only for new sessions
                    txtPaymentAmount.setText(selectedSession.get);*/


        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupComboBoxes();
        loadAllSessions();

        // Setup date picker with current date
        datePicker.setValue(LocalDate.now());
        updateBtn.setDisable(true);
        /*deleteBtn.setDisable(true);*/
    }

    private void loadAllSessions() {
        try {
            List<TherapySessionDto> sessions = therapySessionBO.getAllSessions();
            System.out.println("Loaded sessions: " + sessions.size());

            sessionsList.clear();
            for (TherapySessionDto session : sessions) {
                TherapySessionTm tm = new TherapySessionTm(
                        session.getPatientId(),
                        session.getTherapistId(),
                        session.getPatientName(),
                        session.getTherapistName(),
                        session.getDate(),
                        session.getTime(),
                        session.getStatus(),
                        session.getSessionNote()
                );
                sessionsList.add(tm);
            }

            tblSessions.setItems(sessionsList);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load sessions: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void setupComboBoxes() {
        cmbTime.getItems().addAll(
                "08:00", "09:00", "10:00", "11:00",
                "13:00", "14:00", "15:00", "16:00", "17:00"
        );

        cmbStatus.getItems().addAll("Scheduled", "Completed", "Cancelled", "No-show");

        try {
            List<String> patients = patientBO.getAllPatients().stream()
                    .map(patient -> patient.getId() + " - " + patient.getName())
                    .collect(Collectors.toList());
            cmbPatient.getItems().addAll(patients);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load patients: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colTherapistId.setCellValueFactory(new PropertyValueFactory<>("therapistId"));
        colTherapistName.setCellValueFactory(new PropertyValueFactory<>("therapistName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void cmbPatientAction(ActionEvent actionEvent) {
        String patientSelected = cmbPatient.getValue();

        if (patientSelected == null || patientSelected.isEmpty()) {
            // Prevent the error
            return;
        }

        String patientId = patientSelected.split(" - ")[0];

        try {
            cmbTherapist.getItems().clear();
            List<TherapistDto> therapistDtoList = therapistBO.getAllTherapistOptions(patientId);

            for (TherapistDto dto : therapistDtoList) {
                cmbTherapist.getItems().add(dto.getId() + " - " + dto.getName());
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapists: " + e.getMessage()).show();
            e.printStackTrace();
        }

        calculateMinimumPayment();
    }


    private void calculateMinimumPayment() {
        int patientId = Integer.parseInt(cmbPatient.getValue().split(" - ")[0]);
        int remainingSessions = patientBO.getRemainingSessions(patientId);
        System.out.println("Remaining sessions: " + remainingSessions);
        if (remainingSessions <= 0) {
            txtPaymentAmount.setText("0.00");
            return;
        }

        PaymentDto paymentDto = paymentBO.getPaymentByPatientId(patientId);
        if (paymentDto != null) {
            BigDecimal minimumPayment = paymentDto.getBalancePayment().divide(BigDecimal.valueOf(remainingSessions), 2, BigDecimal.ROUND_HALF_UP);
            System.out.println("Minimum payment: " + minimumPayment);
            // Round up to next whole number if not already whole
            BigDecimal roundedPayment = minimumPayment.setScale(0, RoundingMode.CEILING);
            System.out.println("Rounded payment: " + roundedPayment);
            minimumPaymentAmount = roundedPayment;

            /*txtPaymentAmount.setText(String.valueOf(roundedPayment));*/
            minAmountLabel.setText(" "+String.valueOf(roundedPayment)+".00");
        } else {
            txtPaymentAmount.setText("0.00");
        }
    }

    private TherapySessionDto collectSessionData() {
        TherapySessionDto sessionDTO = new TherapySessionDto();

        // Parse patient ID from combobox (format: "ID - Name")
        String patientSelection = cmbPatient.getValue();
        int patientId = Integer.parseInt(patientSelection.split(" - ")[0]);
        sessionDTO.setPatientId(patientId);

        // Parse therapist ID from combobox
        String therapistSelection = cmbTherapist.getValue();
        int therapistId = Integer.parseInt(therapistSelection.split(" - ")[0]);
        sessionDTO.setTherapistId(therapistId);

        // Set date, time, and status
        sessionDTO.setDate(datePicker.getValue().format(DateTimeFormatter.ISO_DATE));
        sessionDTO.setTime(cmbTime.getValue());
        sessionDTO.setStatus(cmbStatus.getValue());

        // Set session notes
        sessionDTO.setSessionNote(txtSessionNotes.getText().trim());

        // Set names for display purposes
        sessionDTO.setPatientName(patientSelection.split(" - ")[1]);
        sessionDTO.setTherapistName(therapistSelection.split(" - ")[1]);

        boolean conflict = handleDateTime(sessionDTO);
        if (conflict) {
            /*new Alert(Alert.AlertType.ERROR, "Session conflict! Either the patient or therapist has another session at this time.").show();*/
            return null; // stop further execution
        }

        return sessionDTO;
    }

    private TherapySessionDto collectSessionDataUpdate() {
        TherapySessionDto sessionDTO = new TherapySessionDto();

        // Parse patient ID from combobox (format: "ID - Name")
        String patientSelection = cmbPatient.getValue();
        int patientId = Integer.parseInt(patientSelection.split(" - ")[0]);
        sessionDTO.setPatientId(patientId);

        // Parse therapist ID from combobox
        String therapistSelection = cmbTherapist.getValue();
        int therapistId = Integer.parseInt(therapistSelection.split(" - ")[0]);
        sessionDTO.setTherapistId(therapistId);

        // Set date, time, and status
        sessionDTO.setDate(datePicker.getValue().format(DateTimeFormatter.ISO_DATE));
        sessionDTO.setTime(cmbTime.getValue());
        sessionDTO.setStatus(cmbStatus.getValue());

        // Set session notes
        sessionDTO.setSessionNote(txtSessionNotes.getText().trim());

        // Set names for display purposes
        sessionDTO.setPatientName(patientSelection.split(" - ")[1]);
        sessionDTO.setTherapistName(therapistSelection.split(" - ")[1]);

        TherapySessionId id=new TherapySessionId(
                sessionDTO.getTherapistId(),
                sessionDTO.getPatientId()
        );

        boolean conflict = handleDateTimeUpdate(sessionDTO,id);
        if (conflict) {
            /*new Alert(Alert.AlertType.ERROR, "Session conflict! Either the patient or therapist has another session at this time.").show();*/
            return null; // stop further execution
        }

        return sessionDTO;
    }

    private boolean handleDateTime(TherapySessionDto sessionDto) {
        boolean conflictExists = therapySessionBO.isSessionConflict(
                sessionDto.getPatientId(),
                sessionDto.getTherapistId(),
                sessionDto.getDate(),
                sessionDto.getTime()
        );

        if (conflictExists) {
            new Alert(Alert.AlertType.ERROR, "Session conflict! Either the patient or therapist has another session at this time.").show();
            return conflictExists; // stop further execution
        }
        return conflictExists;
    }

    private boolean handleDateTimeUpdate(TherapySessionDto sessionDto, TherapySessionId id) {
        boolean conflictExists = therapySessionBO.isSessionConflictUpdate(
                sessionDto.getPatientId(),
                sessionDto.getTherapistId(),
                sessionDto.getDate(),
                sessionDto.getTime(),
                id
        );

        if (conflictExists) {
            new Alert(Alert.AlertType.ERROR, "Session conflict! Either the patient or therapist has another session at this time.").show();
            return conflictExists; // stop further execution
        }
        return conflictExists;
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (cmbPatient.getValue() == null) {
            errors.append("Patient must be selected\n");
        }

        if (cmbTherapist.getValue() == null) {
            errors.append("Therapist must be selected\n");
        }

        if (datePicker.getValue() == null) {
            errors.append("Date must be selected\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("Session date cannot be in the past\n");
        }

        if (cmbTime.getValue() == null) {
            errors.append("Time must be selected\n");
        }

        if (cmbStatus.getValue() == null) {
            errors.append("Status must be selected\n");
        }

        if (!txtPaymentAmount.getText().isEmpty()) {
            try {
                double amount = Double.parseDouble(txtPaymentAmount.getText().trim());
                if (amount < 0 || amount < minimumPaymentAmount.doubleValue()) {
                    errors.append("Payment amount must be greater than or equal to " + minimumPaymentAmount + "\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Payment amount must be a valid number\n");
            }
        }

        if (errors.length() > 0) {
            new Alert(Alert.AlertType.ERROR, errors.toString()).show();
            return false;
        }

        return true;
    }



}
