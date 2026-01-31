package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.sql.SQLException;
import java.util.List;

import app.SceneManager;
import service.DBService;

public class EditPatientMedicalDataController {
    private int patientId;

    @FXML
    private Label titleLabel;
    @FXML
    private Label patientNameLabel;
    @FXML
    private ComboBox<String> diseaseComboBox;
    @FXML
    private ListView<String> diseaseListView;
    @FXML
    private ComboBox<String> allergyComboBox;
    @FXML
    private ListView<String> allergyListView;

    public void setPatientId(int patientId) {
        this.patientId = patientId;
        loadPatientData();
    }

    @FXML
    private void initialize() {
        titleLabel.setText("Edycja danych medycznych pacjenta");
        try {
            List<String> allDiseases = DBService.getInstance().getAllDiseases();
            diseaseComboBox.getItems().addAll(allDiseases);

            List<String> allAllergies = DBService.getInstance().getAllAllergies();
            allergyComboBox.getItems().addAll(allAllergies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadPatientData(){
        try {
            String patientName = DBService.getInstance().getPatientNameById(patientId);
            patientNameLabel.setText("Pacjent: " + patientName);

            List<String> patientDiseases = DBService.getInstance().getDiseasesByPatientId(patientId);
            diseaseListView.getItems().addAll(patientDiseases);

            List<String> patientAllergies = DBService.getInstance().getAllergiesByPatientId(patientId);
            allergyListView.getItems().addAll(patientAllergies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDoctorDashboard() {
        SceneManager.showDoctorDashboard();
    }
    @FXML
    private void handleAddDisease() {
        String disease = diseaseComboBox.getValue();
        if (disease != null && !diseaseListView.getItems().contains(disease)) {
            diseaseListView.getItems().add(disease);
            try {
                DBService.getInstance().addDiseaseToPatient(patientId, disease);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleRemoveDisease() {
        String selectedDisease = diseaseListView.getSelectionModel().getSelectedItem();
        if (selectedDisease != null) {
            diseaseListView.getItems().remove(selectedDisease);
            try {
                DBService.getInstance().removeDiseaseFromPatient(patientId, selectedDisease);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleAddAllergy() {
        String allergy = allergyComboBox.getValue();
        if (allergy != null && !allergyListView.getItems().contains(allergy)) {
            allergyListView.getItems().add(allergy);
            try {
                DBService.getInstance().addAllergyToPatient(patientId, allergy);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleRemoveAllergy() {
        String selectedAllergy = allergyListView.getSelectionModel().getSelectedItem();
        if (selectedAllergy != null) {
            allergyListView.getItems().remove(selectedAllergy);
            try {
                DBService.getInstance().removeAllergyFromPatient(patientId, selectedAllergy);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
