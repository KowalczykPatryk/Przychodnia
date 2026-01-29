package controller;

import app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import service.AuthService;

public class RegisterController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField peselField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private RadioButton patientRadio;
    @FXML
    private RadioButton doctorRadio;
    @FXML
    private ToggleGroup userTypeGroup;

    private AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        boolean isDoctor = doctorRadio.isSelected();

        authService.register(
                firstNameField.getText(),
                lastNameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                peselField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText());
        SceneManager.showLogin();
    }

    @FXML
    private void goToLogin() {
        SceneManager.showLogin();
    }
}
