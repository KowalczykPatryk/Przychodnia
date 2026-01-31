package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import controller.ConsultationController;
import controller.EditPatientMedicalDataController;

public class SceneManager {

    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void setScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource("/view/" + fxml));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showLogin() {
        setScene("login.fxml");
        stage.setHeight(600);
    }

    public static void showRegister() {
        setScene("register.fxml");
        stage.setHeight(600);
    }

    public static void showPatientDashboard() {
        setScene("patient-dashboard.fxml");
        stage.setHeight(600);
    }

    public static void showDoctorDashboard() {
        setScene("doctor-dashboard.fxml");
        stage.setHeight(600);
    }
    public static void showAddSpecialization() {
        setScene("add-specialization.fxml");
        stage.setHeight(600);
    }
    public static void showEditProfile() {
        setScene("edit-profile.fxml");
        stage.setHeight(600);
    }
    public static void showConsultation(int appointmentId) {
        try {
            FXMLLoader loader =  new FXMLLoader(SceneManager.class.getResource("/view/consultation.fxml"));
            Parent root = loader.load();

            ConsultationController controller = loader.getController();
            controller.setAppointmentId(appointmentId);

            stage.setScene(new Scene(root));
            stage.setHeight(800);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showEditPatientMedicalData(int patientId) {
        try {
            FXMLLoader loader =  new FXMLLoader(SceneManager.class.getResource("/view/edit-patient-medical-data.fxml"));
            Parent root = loader.load();

            EditPatientMedicalDataController controller = loader.getController();
            controller.setPatientId(patientId);

            stage.setScene(new Scene(root));
            stage.setHeight(800);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
