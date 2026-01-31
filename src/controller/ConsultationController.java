package controller;

import app.SceneManager;
import service.DBService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.DatePicker;
import service.DBService;

public class ConsultationController {
    private int appointmentId;

    @FXML
    private CheckBox addPrescriptionCheckBox;
    @FXML
    private VBox prescriptionFieldsContainer;
    @FXML
    private Label titleLabel;
    @FXML
    private Label patientNameLabel;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private ComboBox<String> medicationComboBox;
    @FXML
    private ListView<String> prescriptionListView;
    @FXML
    private TextField prescriptionNumberField;
    @FXML
    private TextField validityField;
    @FXML
    private ComboBox<String> examinationComboBox;
    @FXML
    private ListView<String> examinationListView;
    @FXML
    private CheckBox addExaminationCheckBox;
    @FXML
    private VBox addExaminationFieldsContainer;

    @FXML
    private void initialize() {
        try {
            loadMedications();
            loadExaminations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
        try {
            setLabels();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDoctorDashboard() {
        SceneManager.showDoctorDashboard();
    }

    @FXML
    private void handlePrescriptionCheckBox() {
        boolean isSelected = addPrescriptionCheckBox.isSelected();
        prescriptionFieldsContainer.setVisible(isSelected);
        prescriptionFieldsContainer.setManaged(isSelected);
    }
    @FXML
    private void handleExaminationCheckBox() {
        boolean isSelected = addExaminationCheckBox.isSelected();
        addExaminationFieldsContainer.setVisible(isSelected);
        addExaminationFieldsContainer.setManaged(isSelected);
    }
    @FXML
    private void handleAddMedication() {
        String selectedMedication = medicationComboBox.getValue();
        if (selectedMedication != null && !selectedMedication.isEmpty()) {
            prescriptionListView.getItems().add(selectedMedication);
        }
    }
    @FXML
    private void handleRemoveMedication() {
        String selectedMedication = prescriptionListView.getSelectionModel().getSelectedItem();
        if (selectedMedication != null) {
            prescriptionListView.getItems().remove(selectedMedication);
        }
    }
    @FXML
    private void handleSaveConsultation() {
        try {
            DBService dbService = DBService.getInstance();

            if (appointmentDatePicker.getValue() != null) {
                dbService.updateConsultationDate(appointmentId, appointmentDatePicker.getValue().toString());
            }
            if (startTimeField.getText() != null && !startTimeField.getText().isEmpty()) {
                dbService.updateConsultationStartTime(appointmentId, startTimeField.getText());
            }
            if (endTimeField.getText() != null && !endTimeField.getText().isEmpty()) {
                dbService.updateConsultationEndTime(appointmentId, endTimeField.getText());
            }

            if (addPrescriptionCheckBox.isSelected()) {
                List<String> medications = new ArrayList<>(prescriptionListView.getItems());
                if (!medications.isEmpty()) {
                    String prescriptionNumber = prescriptionNumberField.getText();
                    int validityDays = Integer.parseInt(validityField.getText());

                    dbService.savePrescription(appointmentId, medications, prescriptionNumber, validityDays);
                }

            }

            if (addExaminationCheckBox.isSelected()) {
                List<String> examinations = new ArrayList<>(examinationListView.getItems());
                if (!examinations.isEmpty()) {
                    dbService.saveExaminations(appointmentId, examinations);
                }
            }
            dbService.updateAppointmentStatus(appointmentId, "Zakończona");

            SceneManager.showDoctorDashboard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddExamination() {
        String selectedExamination = examinationComboBox.getValue();
        if (selectedExamination != null && !selectedExamination.isEmpty()) {
            examinationListView.getItems().add(selectedExamination);
        }
    }
    @FXML
    private void handleRemoveExamination() {
        String selectedExamination = examinationListView.getSelectionModel().getSelectedItem();
        if (selectedExamination != null) {
            examinationListView.getItems().remove(selectedExamination);
        }
    }

    private void loadMedications() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<String> medications = FXCollections.observableArrayList(dbService.loadMedications());
        medicationComboBox.setItems(medications);
    }
    private void loadExaminations() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<String> examinations = FXCollections.observableArrayList(dbService.loadExaminations());
        examinationComboBox.setItems(examinations);
    }
    private void setLabels() throws SQLException {
        titleLabel.setText("Konsultacja - ID: " + appointmentId);
        DBService dbService = DBService.getInstance();
        String patientName = dbService.getPatientNameByAppointmentId(appointmentId);
        patientNameLabel.setText("Pacjent: " + patientName);
    }
}