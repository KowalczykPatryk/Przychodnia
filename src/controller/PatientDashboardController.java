package controller;

import app.SceneManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
    private void initialize() {
        welcomeLabel.setText("Witaj, " + ProfileService.getFirstName() + " " + ProfileService.getLastName());

        setupDoctorsTable();

        try {
            loadAppointments();
            loadAvailableDoctors();
            loadProfileInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDoctorsTable() {
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(0)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(1)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(2)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        ((TableColumn<List<String>, String>) doctorsTable.getColumns().get(3)).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    }

    @FXML
    private void handleLogout() {
        ProfileService.clearProfile();
        SceneManager.showLogin();
    }

    @FXML
    private void handleCancelAppointment() {
        System.out.println("Cancel appointment clicked");
    }

    @FXML
    private void handleBookAppointment() {
        List<String> selectedDoctor = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) 
        {
            String doctorId = selectedDoctor.get(0);
        }

    }

    @FXML
    private void handleEditProfile() {

        System.out.println("Edit profile clicked");
    }

    @FXML
    private void handleCheckAvailability() {

        System.out.println("Check availability clicked");
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

    private void loadProfileInfo() {

        profileInfoLabel.setText("Imie: " + ProfileService.getFirstName() +
                "\nNazwisko: " + ProfileService.getLastName() +
                "\nPESEL: " + ProfileService.getPesel() +
                "\nTelefon: " + ProfileService.getPhone() +
                "\nEmail: " + ProfileService.getEmail());
    }
}
