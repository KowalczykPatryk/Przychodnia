package controller;

import javafx.scene.Scene;
import javafx.scene.control.TextField;

import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import java.sql.SQLException;

import app.SceneManager;
import service.DBService;
import service.ProfileService;

public class AddSpecializationController 
{

    @FXML
    private ComboBox<String> nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField durationField;

    @FXML
    private void initialize() 
    {
        loadSpecializationsFromDatabase();
    }

    @FXML
    private void loadSpecializationsFromDatabase()
    {
        ObservableList<String> specializations;
        try
        {
            DBService dbService = DBService.getInstance();
            specializations = FXCollections.observableArrayList(dbService.loadSpecializations());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            specializations = FXCollections.observableArrayList();
        }
        
        nameField.setItems(specializations);    
    }

    @FXML
    private void addSpecialization() 
    {
        try 
        {
            DBService dbService = DBService.getInstance();
            
            dbService.addSpecialization(
                Integer.parseInt(ProfileService.getId()),
                nameField.getValue(),
                descriptionField.getText(),
                Integer.parseInt(durationField.getText())
            );
            SceneManager.showDoctorDashboard();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToDoctorDashboard() 
    {
        SceneManager.showDoctorDashboard();
    }
    
}
