package com.cityhospital.ui;

import com.cityhospital.model.Appointment;
import com.cityhospital.model.Doctor;
import com.cityhospital.model.Patient;
import com.cityhospital.service.FileManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * StatisticsView displays summary statistics with enhanced card visuals.
 * Shows total patients, doctors, appointments, completed, and cancelled counts.
 */
public class StatisticsView {

    /**
     * Creates and returns the statistics view with modern card layout.
     * @return VBox containing the statistics display
     */
    public VBox getView() {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("SYSTEM STATISTICS");
        title.getStyleClass().add("page-title");

        // Load data
        List<Patient> patients = FileManager.loadPatients();
        List<Doctor> doctors = FileManager.loadDoctors();
        List<Appointment> appointments = FileManager.loadAppointments();

        long total = appointments.size();
        long completed = appointments.stream().filter(a -> Appointment.STATUS_COMPLETED.equals(a.getStatus())).count();
        long cancelled = appointments.stream().filter(a -> Appointment.STATUS_CANCELLED.equals(a.getStatus())).count();
        long scheduled = appointments.stream().filter(a -> Appointment.STATUS_SCHEDULED.equals(a.getStatus())).count();

        // Stats grid - 3 columns x 2 rows
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(25);
        statsGrid.setVgap(25);
        statsGrid.setAlignment(Pos.CENTER);

        statsGrid.add(createStatCard("\uD83D\uDC65", "Total Patients", String.valueOf(patients.size()), "#3498DB"), 0, 0);
        statsGrid.add(createStatCard("\uD83D\uDC69\u200D\u2695\uFE0F", "Total Doctors", String.valueOf(doctors.size()), "#8B0000"), 1, 0);
        statsGrid.add(createStatCard("\uD83D\uDCC5", "Total Appointments", String.valueOf(total), "#2C3E50"), 2, 0);
        statsGrid.add(createStatCard("\uD83D\uDD14", "Scheduled", String.valueOf(scheduled), "#F39C12"), 0, 1);
        statsGrid.add(createStatCard("\u2705", "Completed", String.valueOf(completed), "#27AE60"), 1, 1);
        statsGrid.add(createStatCard("\u274C", "Cancelled", String.valueOf(cancelled), "#E74C3C"), 2, 1);

        view.getChildren().addAll(title, statsGrid);
        return view;
    }

    /**
     * Creates an enhanced statistics card with icon, label, and value.
     * Each card has a colored accent bar at the top.
     */
    private VBox createStatCard(String icon, String label, String value, String color) {
        // Accent bar at top
        Region accentBar = new Region();
        accentBar.setPrefHeight(5);
        accentBar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12 12 0 0;");

        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 32));

        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 38));
        valueLabel.setTextFill(Color.web(color));

        // Description
        Label descLabel = new Label(label);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        descLabel.setTextFill(Color.valueOf("#7F8C8D"));

        VBox card = new VBox(8, accentBar, iconLabel, valueLabel, descLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(5, 40, 30, 40));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);" +
            "-fx-border-color: #F0F0F0; -fx-border-width: 1; -fx-border-radius: 12;"
        );
        card.setPrefWidth(240);
        card.setPrefHeight(180);

        return card;
    }
}
