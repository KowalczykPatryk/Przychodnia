package controller;

import app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import java.sql.SQLException;

import service.AuthService;
import service.ProfileService;
import service.DBService;

public class LoginController 
{

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField peselField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private RadioButton patientRadio;
    @FXML
    private RadioButton doctorRadio;
    @FXML
    private ToggleGroup userTypeGroup;

    private AuthService authService = new AuthService();
    private ProfileService profileService = ProfileService.getInstance();
    private DBService dbService;

    public LoginController() throws SQLException {
        dbService = DBService.getInstance();
    }

    @FXML
    private void handleLogin() 
    {
        if (authService.login(
            firstNameField.getText(), 
            lastNameField.getText(),
            peselField.getText(),
            passwordField.getText(),
            doctorRadio.isSelected())
            ) 
        {
            profileService.setProfile(
                "",
                firstNameField.getText(), 
                lastNameField.getText(), 
                peselField.getText(),
                "",
                "",
                doctorRadio.isSelected()
            );

            if (doctorRadio.isSelected()) {
                SceneManager.showDoctorDashboard();
            } else {
                SceneManager.showPatientDashboard();
            }
        }
    }

    @FXML
    private void goToRegister() {
        SceneManager.showRegister();
    }
}
