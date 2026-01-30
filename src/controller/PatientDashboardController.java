package controller;

import app.SceneManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import service.DBService;
import java.sql.SQLException;
import java.util.List;

import service.ProfileService;

public class PatientDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label profileInfoLabel;
    @FXML
    private TableView<List<String>> appointmentsTable;
    @FXML
    private TableView<List<String>> doctorsTable;
    @FXML
    private TableView<List<String>> prescriptionsTable;
    @FXML
    private TableView<List<String>> medicationHistoryTable;
    @FXML
    private TableView<List<String>> diseaseHistoryTable;
    @FXML
    private TableView<List<String>> allergyHistoryTable;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Witaj, " + ProfileService.getFirstName() + " " + ProfileService.getLastName());

        setupDoctorsTable();
        setupAppointmentsTable();
        setupPrescriptionsTable();
        setupMedicationHistoryTable();
        setupDiseaseHistoryTable();
        setupAllergyHistoryTable();

        try {
            loadAppointments();
            loadAvailableDoctors();
            loadPrescriptions();
            loadMedicationHistory();
            loadDiseaseHistory();
            loadAllergyHistory();
            loadProfileInfo();
        } catch (Exception e) {
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
    }

    private void setupDoctorsTable() {
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    }

    private void setupPrescriptionsTable() {
        ((TableColumn<List<String>, String>) prescriptionsTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) prescriptionsTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) prescriptionsTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) prescriptionsTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        ((TableColumn<List<String>, String>) prescriptionsTable.getColumns().get(4)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
    }

    private void setupMedicationHistoryTable() {
        ((TableColumn<List<String>, String>) medicationHistoryTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) medicationHistoryTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) medicationHistoryTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
    }

    private void setupDiseaseHistoryTable() {
        ((TableColumn<List<String>, String>) diseaseHistoryTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) diseaseHistoryTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) diseaseHistoryTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) diseaseHistoryTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    }

    private void setupAllergyHistoryTable() {
        ((TableColumn<List<String>, String>) allergyHistoryTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) allergyHistoryTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
    }

    @FXML
    private void handleLogout() {
        ProfileService.clearProfile();
        SceneManager.showLogin();
    }

    @FXML
    private void handleEditProfile() {
        System.out.println("Edit profile clicked");
    }

    @FXML
    private void handleCancelAppointment() {
        List<String> selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) 
        {
            return;
        }

        String appointmentId = selectedAppointment.get(0);

        try 
        {
            DBService dbService = DBService.getInstance();
            boolean success = dbService.cancelAppointment(Integer.parseInt(appointmentId));
            if (success) 
            {
                loadAppointments();
            } 
            else 
            {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookAppointment() {
        List<String> selectedDoctor = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) 
        {
            return;
        }

        String doctorId = selectedDoctor.get(0);

        if (appointmentDatePicker.getValue() == null) 
        {
            return;
        }

        if (startTimeField.getText().trim().isEmpty() || endTimeField.getText().trim().isEmpty()) 
        {
            return;
        }

        String appointmentDate = appointmentDatePicker.getValue().toString();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        String patientId = ProfileService.getId();

        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) 
        {
            return;
        }

        try 
        {
            DBService dbService = DBService.getInstance();
            boolean success = dbService.bookAppointment(
                    Integer.parseInt(doctorId),
                    Integer.parseInt(patientId),
                    appointmentDate,
                    startTime,
                    endTime);

            if (success) 
            {
                clearAppointmentForm();
                loadAppointments();
            } 
            else 
            {
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private void clearAppointmentForm() {
        appointmentDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        doctorsTable.getSelectionModel().clearSelection();
    }

    private void loadAppointments() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadAppointmentsForPatient(Integer.parseInt(ProfileService.getId())));
        appointmentsTable.setItems(data);
    }

    private void loadAvailableDoctors() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadAvailableDoctors());
        doctorsTable.setItems(data);
    }

    private void loadPrescriptions() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadPrescriptionsForPatient(Integer.parseInt(ProfileService.getId())));
        prescriptionsTable.setItems(data);
    }

    private void loadMedicationHistory() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadMedicationHistoryForPatient(Integer.parseInt(ProfileService.getId())));
        medicationHistoryTable.setItems(data);
    }

    private void loadDiseaseHistory() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadDiseaseHistoryForPatient(Integer.parseInt(ProfileService.getId())));
        diseaseHistoryTable.setItems(data);
    }

    private void loadAllergyHistory() throws SQLException {
        DBService dbService = DBService.getInstance();
        ObservableList<List<String>> data = FXCollections.observableArrayList(dbService.loadAllergyHistoryForPatient(Integer.parseInt(ProfileService.getId())));
        allergyHistoryTable.setItems(data);
    }

    private void loadProfileInfo() {

        profileInfoLabel.setText("Imie: " + ProfileService.getFirstName() +
                "\nNazwisko: " + ProfileService.getLastName() +
                "\nPESEL: " + ProfileService.getPesel() +
                "\nTelefon: " + ProfileService.getPhone() +
                "\nEmail: " + ProfileService.getEmail());
    }
}
