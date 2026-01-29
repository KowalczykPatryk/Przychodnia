package controller;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import app.SceneManager;

public class AddSpecializationController {

    @FXML
    private ComboBox<String> nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField durationField;

    @FXML
    private void initialize() {
        loadSpecializationsFromDatabase();
    }

    @FXML
    private void loadSpecializationsFromDatabase()
    {

        ObservableList<String> specializations = FXCollections.observableArrayList(
            "Kardiologia",
            "Dermatologia",
            "Pediatria",
            "Neurologia",
            "Ortopedia"
        );
        
        nameField.setItems(specializations);    
    }

    @FXML
    private void addSpecialization() {

        System.out.println("Add specialization clicked");
        SceneManager.showDoctorDashboard();
    }
    @FXML
    private void goToDoctorDashboard() {
        SceneManager.showDoctorDashboard();
    }
    
}
