package com.cityhospital;

import com.cityhospital.model.User;
import com.cityhospital.service.*;
import com.cityhospital.ui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * MainApp is the entry point for THE CITY HOSPITAL JavaFX application.
 * 
 * This class:
 * 1. Extends javafx.application.Application (JavaFX entry point)
 * 2. Initializes default user accounts on first run
 * 3. Displays the login screen
 * 4. Manages role-based navigation after successful login
 * 
 * ROLE-BASED ACCESS CONTROL:
 * After authentication, the user is directed to their role-specific dashboard:
 * - ADMIN    -> AdminDashboard
 * - DOCTOR   -> DoctorDashboard
 * - RECEPTIONIST -> ReceptionistDashboard
 * - PATIENT  -> PatientDashboard
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("THE CITY HOSPITAL - Smart Healthcare System");
        stage.setResizable(true);

        // Initialize default user accounts on first run
        AuthenticationService.initializeDefaultUsers();

        // Seed demo data (patients, doctors, appointments) on first run
        DemoDataService.seedDemoData();
        // Add supplemental demo data for empty pages on every startup
        DemoDataService.supplementDemoData();

        // Show the login screen
        showLogin(stage);

        stage.show();

        // Send appointment reminders after the UI is visible
        Platform.runLater(() -> ReminderService.sendStartupReminders());
    }

    /**
     * Displays the login screen on the given stage.
     * Sets up a callback to navigate to the appropriate dashboard on successful login.
     * 
     * @param stage The primary stage
     */
    public static void showLogin(Stage stage) {
        LoginView loginView = new LoginView(stage);
        loginView.setLoginCallback((User user) -> {
            // ROLE-BASED ACCESS CONTROL:
            // Route user to the correct dashboard based on their role
            switch (user.getRole()) {
                case User.ROLE_ADMIN:
                    AdminDashboard adminDash = new AdminDashboard(stage, user);
                    stage.setScene(adminDash.getScene());
                    break;
                case User.ROLE_DOCTOR:
                    DoctorDashboard doctorDash = new DoctorDashboard(stage, user);
                    stage.setScene(doctorDash.getScene());
                    break;
                case User.ROLE_RECEPTIONIST:
                    ReceptionistDashboard receptionDash = new ReceptionistDashboard(stage, user);
                    stage.setScene(receptionDash.getScene());
                    break;
                case User.ROLE_PATIENT:
                    PatientDashboard patientDash = new PatientDashboard(stage, user);
                    stage.setScene(patientDash.getScene());
                    break;
                default:
                    System.err.println("Unknown role: " + user.getRole());
            }
        });
        stage.setScene(loginView.getScene());
    }

    /**
     * Main method - JavaFX entry point.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
