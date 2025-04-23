package org.example.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.example.bo.BOFactory;
import org.example.bo.custom.BOTypes;
import org.example.bo.custom.LoginBO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    LoginBO loginBO = (LoginBO) BOFactory.getInstance().getBO(BOTypes.LOGIN);
    private String role;
    private boolean isPasswordVisible = false;

    @FXML
    private AnchorPane ancLogin;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button passwordVisibilityButton;

    @FXML
    private HBox passwordHBox;

    @FXML
    private ImageView passwordVisibilityIcon;

    @FXML
    private TextField showPasswordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void LoginAction(ActionEvent event) throws IOException {
        /*try {
            Session session = FactoryConfiguration.getInstance().getSession();

            Query<Users> query = session.createQuery("from Users u where u.username=:username", Users.class);
            query.setParameter("username", UsernameTextId.getText());
            *//*query.setParameter("password", BCrypt.withDefaults().hashToString(12, PswField.getText().toCharArray()));*//*
            List<Users> list = query.list();

            if (!list.isEmpty()) {
                System.out.println(list.get(0).getRole());
                *//*System.out.println(list.get(0).getPassword());*//*
                if(!list.isEmpty() && BCrypt.verifyer().verify(PswField.getText().toCharArray(), list.get(0).getPassword()).verified) {
                    System.out.println("Login successful!");
                    return;
                } else {
                    System.out.println("Login failed password incorrect");
                    return;
                }

            }
            System.out.println("Login failed Username and Password Incorrect");

            session.close();
        } catch (HibernateException e) {
            System.out.println("Login failed");
            *//*e.printStackTrace();*//*
        }*/
        boolean authenticate = loginBO.authenticate(usernameTextField.getText(), passwordField.getText());
        if (authenticate) {     /////////////temp/////////////
            System.out.println("Login successful!");
            role = loginBO.getRole();  /////////////temp/////////////
            System.out.println("role-"+role);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
            AnchorPane newPane=fxmlLoader.load();

            AnchorPane.setTopAnchor(newPane, 0.0);
            AnchorPane.setRightAnchor(newPane, 0.0);
            AnchorPane.setBottomAnchor(newPane, 0.0);
            AnchorPane.setLeftAnchor(newPane, 0.0);

            newPane.setTranslateX(ancLogin.getWidth());
            ancLogin.getChildren().setAll(newPane);

            Platform.runLater(() -> {
                Timeline timeline = new Timeline();
                KeyValue keyValue = new KeyValue(newPane.translateXProperty(), 0, Interpolator.EASE_IN);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);
                timeline.getKeyFrames().add(keyFrame);
                timeline.setOnFinished(event1 -> {
                    ancLogin.getChildren().clear();
                    ancLogin.getChildren().add(newPane);
                });
                timeline.play();
            });
        } else {    /////////////temp/////////////
            boolean wrongPsw = loginBO.isWrongPsw();
            if(wrongPsw) {
                usernameTextField.setStyle("-fx-text-box-border: green; -fx-text-inner-color: green;");
                passwordHBox.setStyle("-fx-border-color: red; -fx-text-inner-color: red; -fx-border-radius: 5px;");
            } else {
                usernameTextField.setStyle("-fx-text-box-border: red; -fx-text-inner-color: red;");
                passwordHBox.setStyle("-fx-border-color: red; -fx-text-inner-color: red; -fx-border-radius: 5px;");
            }
            System.out.println("Try again");
        }
    }


    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;

        try {
            if (isPasswordVisible) {
                // Show password
                showPasswordTextField.setVisible(true);
                showPasswordTextField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);

                // Load image with full path checking
                URL visibilityOnURL = getClass().getResource("/assets/icons/icons8-visibility-off-30.png");
                if (visibilityOnURL != null) {
                    passwordVisibilityIcon.setImage(new Image(visibilityOnURL.toString()));
                } else {
                    System.err.println("Visibility on icon not found");
                }
            } else {
                // Hide password
                showPasswordTextField.setVisible(false);
                showPasswordTextField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);

                // Load image with full path checking
                URL visibilityOffURL = getClass().getResource("/assets/icons/icons8-visibility-30.png");
                if (visibilityOffURL != null) {
                    passwordVisibilityIcon.setImage(new Image(visibilityOffURL.toString()));
                } else {
                    System.err.println("Visibility off icon not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind text properties to sync password fields
        showPasswordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }
}
