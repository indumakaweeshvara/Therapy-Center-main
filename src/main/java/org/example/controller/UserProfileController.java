package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.LoginBO;
import org.example.dto.UserDto;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    public AnchorPane ContentAnchor;
    LoginBO loginBO = (LoginBO) BOFactory.getInstance().getBO(BOTypes.LOGIN);

    @FXML
    private Button addReceptionistBtn;

    @FXML
    private Button contactAdminBtn;

    @FXML
    private Button editProfileBtn;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField usernameField;

    @FXML
    void addReceptionistAction(ActionEvent event) throws IOException {
        navigateTo("/view/AddReceptionist.fxml");
    }

    @FXML
    void adminBtnAction(ActionEvent event) throws IOException {
        navigateTo("/view/ContactAdmin.fxml");
    }

    @FXML
    void editProfileAction(ActionEvent event) throws IOException {
        navigateTo("/view/EditUserProfile.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(Objects.equals(loginBO.getRole(), "Admin")){ //////temporary////////
            contactAdminBtn.setVisible(false);
        }else{
            addReceptionistBtn.setDisable(true);
        }

        UserDto user = loginBO.getUser();
        if (user != null) {
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            usernameField.setText(user.getUsername());
            roleField.setText(user.getRole());
        }


    }

    public void navigateTo(String fxmlPath) throws IOException {
        try {
            ContentAnchor.getChildren().clear();
            AnchorPane load = FXMLLoader.load(getClass().getResource(fxmlPath));

            load.prefWidthProperty().bind(ContentAnchor.widthProperty());
            load.prefHeightProperty().bind(ContentAnchor.heightProperty());

            ContentAnchor.getChildren().add(load);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load page!").show();
        }
    }


}
