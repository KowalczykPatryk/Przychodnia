package controller;

import app.SceneManager;
import service.DBService;
import service.ProfileService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.SQLException;

import service.ProfileService;
import app.SceneManager;
import java.util.List;

public class DoctorDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label profileInfoLabel;
    @FXML
    private TableView<List<String>> appointmentsTable;
    @FXML
    private TableView<List<String>> patientsTable;

    @FXML
    private void initialize() {

        welcomeLabel.setText("Witaj, Dr. " + ProfileService.getLastName());

        setupAppointmentsTable();
        setupPatientsTable();

        try {
            loadAppointments();
            loadPatients();
            loadProfileInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAppointmentsTable() {
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(4)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(5)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(6)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(7)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(7)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(8)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(8)));
        ((TableColumn<List<String>, String>) appointmentsTable.getColumns().get(9)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(9)));
    }

    private void setupPatientsTable() {
        ((TableColumn<List<String>, String>) patientsTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) patientsTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) patientsTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) patientsTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    }

    @FXML
    private void handleLogout() {
        SceneManager.showLogin();
    }

    @FXML
    private void handleStartConsultation() {
        List<String> selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null && !selectedAppointment.getLast().equals("Anulowana") && !selectedAppointment.getLast().equals("Zakończona")) {
            int appointmentId = Integer.parseInt(selectedAppointment.get(0));
            SceneManager.showConsultation(appointmentId);
        }
    }

    @FXML
    private void handleEditPatientMedicalData() {
        List<String> selectedPatient = patientsTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            int patientId = Integer.parseInt(selectedPatient.get(0));
            SceneManager.showEditPatientMedicalData(patientId);
        }
    }

    @FXML
    private void handleAddSpecialization() {

        SceneManager.showAddSpecialization();
    }

    private void loadAppointments() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadAppointmentsForDoctor(Integer.parseInt(ProfileService.getId())));
        appointmentsTable.setItems(data);
    }

    private void loadPatients() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadPatientsForDoctor(Integer.parseInt(ProfileService.getId())));
        patientsTable.setItems(data);
    }

    private void loadProfileInfo() throws SQLException {
        DBService dbService = DBService.getInstance();
        List<List<String>> specializations = dbService.loadSpecializationsForDoctor(Integer.parseInt(ProfileService.getId()));
        String specializationText = "";
        for (List<String> spec : specializations) 
        {
            specializationText += "\tNazwa: ";
            specializationText += spec.get(0);
            specializationText += "\n";
            specializationText += "\tOpis: ";
            specializationText += spec.get(1);
            specializationText += "\n";
            specializationText += "\tCzas trwania: ";
            specializationText += spec.get(2);
            specializationText += "\n\n";

        }
        profileInfoLabel.setText(
            "Imie:" + ProfileService.getFirstName() +
            "\nNazwisko: " + ProfileService.getLastName() +
            "\nPESEL: " + ProfileService.getPesel() +
            "\nTelefon: " + ProfileService.getPhone() +
            "\nEmail: " + ProfileService.getEmail() +
            "\nSpecjalizacje:\n" + specializationText
        );
    }
}
