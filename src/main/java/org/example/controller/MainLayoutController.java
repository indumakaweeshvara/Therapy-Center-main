package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.LoginBO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    LoginBO loginBO = (LoginBO) BOFactory.getInstance().getBO(BOTypes.LOGIN);

    @FXML
    public Button therapyDetailsBtn;

    @FXML
    private Button AccountBtn;

    @FXML
    private AnchorPane ContentAnchor;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button appoinmentsBtn;

    @FXML
    private Button patientsBtn;

    @FXML
    private Button paymentBtn;

    @FXML
    private Button programsBtn;

    @FXML
    private Button reportBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button therapistBtn;

    @FXML
    private AnchorPane DashBoardAnc;

    @FXML
    void AccountBtnAction(ActionEvent event) {
        navigateTo("/view/UserProfile.fxml");
    }

    @FXML
    void LogoutBtnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        AnchorPane newPane = loader.load();
        newPane.prefWidthProperty().bind(DashBoardAnc.widthProperty());
        newPane.prefHeightProperty().bind(DashBoardAnc.heightProperty());

        // Set anchors to make the newPane fill the mainAnchorPane
        AnchorPane.setTopAnchor(newPane, 0.0);
        AnchorPane.setRightAnchor(newPane, 0.0);
        AnchorPane.setBottomAnchor(newPane, 0.0);
        AnchorPane.setLeftAnchor(newPane, 0.0);

        // Clear the current pane (if needed) and add the new one
        DashBoardAnc.getChildren().setAll(newPane);
    }

    @FXML
    void appoinmentsBtnAction(ActionEvent event) {
        navigateTo("/view/TherapySession.fxml");
    }

    @FXML
    void patientsBtnAction(ActionEvent event) {
        navigateTo("/view/Patient.fxml");
    }

    @FXML
    void paymentBtnAction(ActionEvent event) {
        navigateTo("/view/Payment.fxml");
    }

    @FXML
    void programsBtnAction(ActionEvent event) {
        navigateTo("/view/TherapyProgram.fxml");
    }

    @FXML
    void reoprtBtnAction(ActionEvent event) {

    }

    @FXML
    void settingsBtnAction(ActionEvent event) {

    }

    @FXML
    public void therapyDetailsBtnAction(ActionEvent actionEvent) {
        navigateTo("/view/TherapyDetail.fxml");
    }

    @FXML
    void therapistBtnAction(ActionEvent event) {
        navigateTo("/view/Therapist.fxml");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*String role = *//*loginBO.getRole();*//* "Admin";*/ /////////////temporary assign /////////////
        String role=loginBO.getRole();
        navigateTo("/view/UserProfile.fxml");
        AccountBtn.setText(role);
        if (!role.equals("Admin")) {
            therapistBtn.setDisable(true);
            programsBtn.setDisable(true);

        }

    }

    public void navigateTo(String fxmlPath){
        try{
            ContentAnchor.getChildren().clear();
            AnchorPane load= FXMLLoader.load(getClass().getResource(fxmlPath));

            load.prefWidthProperty().bind(ContentAnchor.widthProperty());
            load.prefHeightProperty().bind(ContentAnchor.heightProperty());

            ContentAnchor.getChildren().add(load);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Fail to load page!").show();
        }
    }


}
