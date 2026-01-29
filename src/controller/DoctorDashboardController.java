package controller;

import app.SceneManager;
import service.ProfileService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import service.ProfileService;
import app.SceneManager;

public class DoctorDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label profileInfoLabel;
    @FXML
    private TableView todayAppointmentsTable;
    @FXML
    private TableView allAppointmentsTable;
    @FXML
    private TableView patientsTable;
    @FXML
    private TableView availabilityTable;

    @FXML
    private void initialize() {

        welcomeLabel.setText("Witaj, Dr. " + ProfileService.getLastName());


        loadTodayAppointments();
        loadAllAppointments();
        loadPatients();
        loadAvailability();
        loadProfileInfo();
    }

    @FXML
    private void handleLogout() {
        SceneManager.showLogin();
    }

    @FXML
    private void handleStartConsultation() {

        System.out.println("Start consultation clicked");
    }

    @FXML
    private void handleViewPatientHistory() {

        System.out.println("View patient history clicked");
    }

    @FXML
    private void handleAddTimeSlot() {

        System.out.println("Add time slot clicked");
    }

    @FXML
    private void handleRemoveTimeSlot() {

        System.out.println("Remove time slot clicked");
    }

    @FXML
    private void handleAddSpecialization() {

        SceneManager.showAddSpecialization();
    }

    private void loadTodayAppointments() {

        System.out.println("Loading today's appointments...");
    }

    private void loadAllAppointments() {

        System.out.println("Loading all appointments...");
    }

    private void loadPatients() {

        System.out.println("Loading patients...");
    }

    private void loadAvailability() {

        System.out.println("Loading availability...");
    }

    private void loadProfileInfo() {

        profileInfoLabel.setText(
                "Imie: Dr. " + ProfileService.getLastName());
    }
}
