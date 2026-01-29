package controller;

import app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import service.ProfileService;

public class PatientDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label profileInfoLabel;
    @FXML
    private TableView appointmentsTable;
    @FXML
    private TableView availableDoctorsTable;
    @FXML
    private TableView medicalHistoryTable;

    private ProfileService profileService = ProfileService.getInstance();

    @FXML
    private void initialize() {

        welcomeLabel.setText("Witaj, " + profileService.getFirstName() + " " + profileService.getLastName());
        
        loadAppointments();
        loadAvailableDoctors();
        loadMedicalHistory();
        loadProfileInfo();
    }

    @FXML
    private void handleLogout() {
        profileService.clearProfile();
        SceneManager.showLogin();
    }

    @FXML
    private void handleCancelAppointment() {
        System.out.println("Cancel appointment clicked");
    }

    @FXML
    private void handleBookAppointment() {
        System.out.println("Book appointment clicked");
    }

    @FXML
    private void handleEditProfile() {

        System.out.println("Edit profile clicked");
    }

    @FXML
    private void handleCheckAvailability() {

        System.out.println("Check availability clicked");
    }

    private void loadAppointments() {

        System.out.println("Loading patient appointments...");
    }

    private void loadAvailableDoctors() {

        System.out.println("Loading available doctors...");
    }

    private void loadMedicalHistory() {

        System.out.println("Loading medical history...");
    }

    private void loadProfileInfo() {

        profileInfoLabel.setText("Imie: " + profileService.getFirstName() +
                                 "\nNazwisko" + profileService.getLastName() + 
                                 "\nPESEL: " + profileService.getPesel() + 
                                 "\nTelefon: " + profileService.getPhone() + 
                                 "\nEmail: " + profileService.getEmail());
    }
}
