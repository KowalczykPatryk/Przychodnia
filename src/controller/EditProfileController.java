package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import app.SceneManager;
import service.ProfileService;
import java.sql.SQLException;
import service.DBService;

public class EditProfileController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    @FXML
    private void goToPatientDashboard() {
        SceneManager.showPatientDashboard();
    }
    @FXML
    private void handleSaveChanges() {
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        try {
            DBService dbService = DBService.getInstance();
            if (!name.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() && !email.isEmpty())
            {
                dbService.updatePatientData(Integer.parseInt(ProfileService.getId()), name, lastName, phone, email);
                goToPatientDashboard();
                ProfileService.setFirstName(name);
                ProfileService.setLastName(lastName);
                ProfileService.setPhone(phone);
                ProfileService.setEmail(email);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}
