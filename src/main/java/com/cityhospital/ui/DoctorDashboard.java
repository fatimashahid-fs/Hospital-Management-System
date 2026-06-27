package com.cityhospital.ui;

import com.cityhospital.controller.*;
import com.cityhospital.model.*;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.MainApp;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

/**
 * DoctorDashboard provides the doctor's interface.
 * ROLE-BASED ACCESS CONTROL: Only DOCTOR role can access.
 */
public class DoctorDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private StackPane contentArea;

    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final PatientController patientController = new PatientController();
    private final PrescriptionController prescriptionController = new PrescriptionController();

    private Doctor currentDoctor;

    public DoctorDashboard(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = new BorderPane();
        List<Doctor> doctors = doctorController.getAllDoctors();
        if (!doctors.isEmpty()) currentDoctor = doctors.get(0);
        buildDashboard();
    }

    private void buildDashboard() {
        VBox topBanner = new VBox(3);
        topBanner.getStyleClass().add("top-banner");
        Label title = new Label("THE CITY HOSPITAL  \u2014  Doctor Dashboard");
        title.getStyleClass().add("top-banner-title");
        String docName = currentDoctor != null ? currentDoctor.getName() : currentUser.getUsername();
        Label userLabel = new Label("Welcome, Dr. " + docName);
        userLabel.getStyleClass().add("top-banner-subtitle");
        topBanner.getChildren().addAll(title, userLabel);

        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Button btnDashboard = createSidebarButton("\uD83C\uDFE0  Dashboard");
        Button btnProfile = createSidebarButton("\uD83D\uDC64  My Profile");
        Button btnAppointments = createSidebarButton("\uD83D\uDCC5  My Appointments");
        Button btnPatients = createSidebarButton("\uD83D\uDC65  Patient Details");
        Button btnPrescribe = createSidebarButton("\uD83D\uDC8A  Prescribe");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("\uD83D\uDEAA  Logout");
        btnLogout.getStyleClass().add("sidebar-button-danger");

        sidebar.getChildren().addAll(btnDashboard, btnProfile, btnAppointments, btnPatients, btnPrescribe, spacer, btnLogout);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setTop(topBanner);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        btnDashboard.setOnAction(e -> showDashboard());
        btnProfile.setOnAction(e -> showProfile());
        btnAppointments.setOnAction(e -> showAppointments());
        btnPatients.setOnAction(e -> showPatientDetails());
        btnPrescribe.setOnAction(e -> showPrescribe());
        btnLogout.setOnAction(e -> logout());

        showDashboard();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("sidebar-button");
        return btn;
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/com/cityhospital/style.css").toExternalForm());
        return scene;
    }

    private void logout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            MainApp.showLogin(stage);
        }
    }

    private void showDashboard() {
        VBox dash = new VBox(20);
        dash.setAlignment(Pos.TOP_CENTER);
        dash.setPadding(new Insets(30, 0, 0, 0));

        String docName = currentDoctor != null ? "Dr. " + currentDoctor.getName() : currentUser.getUsername();
        Label welcome = new Label("Welcome, " + docName + "!");
        welcome.getStyleClass().add("welcome-title");
        Label desc = new Label("Manage your appointments, patient details, and prescriptions.");
        desc.getStyleClass().add("welcome-subtitle");

        String doctorId = currentDoctor != null ? currentDoctor.getDoctorId() : "";
        long myAppts = appointmentController.getAppointmentsByDoctorSorted(doctorId).size();
        long todayAppts = appointmentController.getAppointmentsByDoctorSorted(doctorId).stream()
            .filter(a -> LocalDate.now().toString().equals(String.valueOf(a.getAppointmentDate())))
            .count();
        long pendingCount = appointmentController.getAppointmentsByDoctorSorted(doctorId).stream()
            .filter(a -> Appointment.STATUS_PENDING.equals(a.getStatus())).count();

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(20, 0, 0, 0));
        statsRow.getChildren().addAll(
            createStatCard("📅", String.valueOf(myAppts), "Total Appointments"),
            createStatCard("📋", String.valueOf(todayAppts), "Today's Schedule"),
            createStatCard("⏳", String.valueOf(pendingCount), "Pending"),
            createStatCard("💊", String.valueOf(prescriptionController.getPrescriptionsByDoctor(doctorId).size()), "Prescriptions")
        );

        dash.getChildren().addAll(welcome, desc, statsRow);
        contentArea.getChildren().setAll(dash);
    }

    private VBox createStatCard(String icon, String value, String label) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        Label descLabel = new Label(label);
        descLabel.getStyleClass().add("stat-label");
        VBox card = new VBox(8, iconLabel, valueLabel, descLabel);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("stat-card");
        return card;
    }

    private void showProfile() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("MY PROFILE");
        title.getStyleClass().add("page-title");

        if (currentDoctor == null) {
            Label noDoctor = new Label("No doctor profile found. Please contact admin.");
            noDoctor.setTextFill(Color.valueOf("#E74C3C"));
            view.getChildren().addAll(title, noDoctor);
            contentArea.getChildren().setAll(view);
            return;
        }

        GridPane profileGrid = new GridPane();
        profileGrid.getStyleClass().add("profile-grid");

        profileGrid.add(new Label("Doctor ID:"), 0, 0); profileGrid.add(new Label(currentDoctor.getDoctorId()), 1, 0);
        profileGrid.add(new Label("Name:"), 0, 1); profileGrid.add(new Label(currentDoctor.getName()), 1, 1);
        profileGrid.add(new Label("Specialization:"), 0, 2); profileGrid.add(new Label(currentDoctor.getSpecialization()), 1, 2);
        profileGrid.add(new Label("Phone:"), 0, 3); profileGrid.add(new Label(currentDoctor.getPhone()), 1, 3);
        profileGrid.add(new Label("Email:"), 0, 4); profileGrid.add(new Label(currentDoctor.getEmail()), 1, 4);
        profileGrid.add(new Label("Available Time:"), 0, 5); profileGrid.add(new Label(currentDoctor.getAvailableTime()), 1, 5);
        for (var node : profileGrid.lookupAll(".label")) {
            if (node instanceof Label l) {
                String txt = l.getText();
                if (txt != null && txt.endsWith(":")) l.getStyleClass().add("profile-label");
                else l.getStyleClass().add("profile-value");
            }
        }

        // Card wrapper for profile
        VBox profileCard = new VBox(profileGrid);
        profileCard.getStyleClass().add("card");

        // Update available time section
        Label updateLabel = new Label("UPDATE AVAILABLE TIME");
        updateLabel.getStyleClass().add("form-section");

        HBox timeRow = new HBox(10);
        TextField timeField = new TextField(currentDoctor.getAvailableTime());
        timeField.setPromptText("HH:mm (09:00-17:00)");
        timeField.getStyleClass().add("text-field");
        timeField.setPrefWidth(200);
        Button btnUpdateTime = new Button("Update Time");
        btnUpdateTime.getStyleClass().addAll("btn", "btn-primary");
        timeRow.getChildren().addAll(timeField, btnUpdateTime);

        VBox updateCard = new VBox(10, updateLabel, timeRow);
        updateCard.getStyleClass().add("card");

        btnUpdateTime.setOnAction(e -> {
            if (!ValidationService.isValidTime(timeField.getText())) {
                AlertUtil.showError("Error", "Invalid time. Use HH:mm (09:00-17:00).");
                return;
            }
            currentDoctor.setAvailableTime(timeField.getText().trim());
            doctorController.updateDoctor(currentDoctor);
            showProfile();
        });

        view.getChildren().addAll(title, profileCard, updateCard);
        contentArea.getChildren().setAll(view);
    }

    private void showAppointments() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("MY APPOINTMENTS");
        title.getStyleClass().add("page-title");

        String doctorId = currentDoctor != null ? currentDoctor.getDoctorId() : "";

        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Appointment, String> colId = new TableColumn<>("Appt ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient ID");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colUrgency = new TableColumn<>("Priority");
        colUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colId, colPId, colDate, colTime, colUrgency, colStatus);

        // Color-code rows by urgency
        table.setRowFactory(tv -> new TableRow<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (Appointment.URGENCY_EMERGENCY.equals(item.getUrgency())) {
                    setStyle("-fx-background-color: #FFE0E0;");
                } else if (Appointment.URGENCY_URGENT.equals(item.getUrgency())) {
                    setStyle("-fx-background-color: #FFF3CD;");
                } else {
                    setStyle("");
                }
            }
        });

        table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByDoctorSorted(doctorId)));

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnComplete = new Button("Mark as Completed"); btnComplete.getStyleClass().addAll("btn", "btn-success");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnComplete, btnRefresh);

        btnComplete.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                appointmentController.completeAppointment(sel.getAppointmentId());
                table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByDoctorSorted(doctorId)));
            } else AlertUtil.showError("Error", "Select an appointment.");
        });
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByDoctorSorted(doctorId))));

        view.getChildren().addAll(title, table, btns);
        contentArea.getChildren().setAll(view);
    }

    private void showPatientDetails() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("PATIENT DETAILS");
        title.getStyleClass().add("page-title");

        HBox searchRow = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Patient ID to view details...");
        searchField.getStyleClass().add("search-field");
        Button btnSearch = new Button("View Patient");
        btnSearch.getStyleClass().addAll("btn", "btn-primary");
        searchRow.getChildren().addAll(searchField, btnSearch);

        GridPane detailsGrid = new GridPane();
        detailsGrid.getStyleClass().add("profile-grid");
        detailsGrid.setPadding(new Insets(20, 0, 0, 0));

        Label lblId = new Label(); Label lblName = new Label();
        Label lblAge = new Label(); Label lblGender = new Label();
        Label lblPhone = new Label(); Label lblEmail = new Label();
        Label lblHistory = new Label();

        detailsGrid.add(new Label("Patient ID:"), 0, 0); detailsGrid.add(lblId, 1, 0);
        detailsGrid.add(new Label("Name:"), 0, 1); detailsGrid.add(lblName, 1, 1);
        detailsGrid.add(new Label("Age:"), 0, 2); detailsGrid.add(lblAge, 1, 2);
        detailsGrid.add(new Label("Gender:"), 0, 3); detailsGrid.add(lblGender, 1, 3);
        detailsGrid.add(new Label("Phone:"), 0, 4); detailsGrid.add(lblPhone, 1, 4);
        detailsGrid.add(new Label("Email:"), 0, 5); detailsGrid.add(lblEmail, 1, 5);
        detailsGrid.add(new Label("History:"), 0, 6); detailsGrid.add(lblHistory, 1, 6);

        VBox detailsCard = new VBox(detailsGrid);
        detailsCard.getStyleClass().add("card");
        detailsCard.setVisible(false);
        detailsCard.setManaged(false);

        // Prescription history for the searched patient
        TableView<Prescription> rxTable = new TableView<>();
        rxTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rxTable.getStyleClass().add("table-view");
        TableColumn<Prescription, String> rxMed = new TableColumn<>("Medicine");
        rxMed.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<Prescription, String> rxDos = new TableColumn<>("Dosage");
        rxDos.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        TableColumn<Prescription, String> rxInst = new TableColumn<>("Instructions");
        rxInst.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        TableColumn<Prescription, String> rxDate = new TableColumn<>("Date");
        rxDate.setCellValueFactory(new PropertyValueFactory<>("datePrescribed"));
        rxTable.getColumns().addAll(rxMed, rxDos, rxInst, rxDate);

        VBox rxCard = new VBox(10, new Label("PRESCRIPTION HISTORY"), rxTable);
        rxCard.getStyleClass().add("card");
        rxCard.setVisible(false);
        rxCard.setManaged(false);

        btnSearch.setOnAction(e -> {
            String pid = searchField.getText().trim();
            if (pid.isEmpty()) { AlertUtil.showError("Error", "Enter a Patient ID."); return; }
            Patient patient = patientController.getPatientById(pid);
            if (patient != null) {
                lblId.setText(patient.getPatientId()); lblName.setText(patient.getName());
                lblAge.setText(String.valueOf(patient.getAge())); lblGender.setText(patient.getGender());
                lblPhone.setText(patient.getPhone()); lblEmail.setText(patient.getEmail());
                lblHistory.setText(patient.getMedicalHistory());
                detailsCard.setVisible(true); detailsCard.setManaged(true);
                rxTable.setItems(FXCollections.observableArrayList(prescriptionController.getPrescriptionsByPatient(pid)));
                rxCard.setVisible(true); rxCard.setManaged(true);
            } else {
                AlertUtil.showError("Not Found", "Patient ID " + pid + " not found.");
                detailsCard.setVisible(false); detailsCard.setManaged(false);
                rxCard.setVisible(false); rxCard.setManaged(false);
            }
        });

        view.getChildren().addAll(title, searchRow, detailsCard, rxCard);
        contentArea.getChildren().setAll(view);
    }

    private void showPrescribe() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("PRESCRIBE MEDICINE");
        title.getStyleClass().add("page-title");

        if (currentDoctor == null) {
            view.getChildren().addAll(title, new Label("No doctor profile found."));
            contentArea.getChildren().setAll(view);
            return;
        }

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        TextField fPatientId = new TextField(); fPatientId.setPromptText("Patient ID"); fPatientId.getStyleClass().add("text-field");
        TextField fMedicine = new TextField(); fMedicine.setPromptText("Medicine Name"); fMedicine.getStyleClass().add("text-field");
        TextField fDosage = new TextField(); fDosage.setPromptText("e.g. 500mg"); fDosage.getStyleClass().add("text-field");
        TextField fInstructions = new TextField(); fInstructions.setPromptText("e.g. Take 1 tablet twice daily"); fInstructions.getStyleClass().add("text-field");

        form.add(new Label("Patient ID:"), 0, 0); form.add(fPatientId, 1, 0);
        form.add(new Label("Medicine:"), 0, 1); form.add(fMedicine, 1, 1);
        form.add(new Label("Dosage:"), 0, 2); form.add(fDosage, 1, 2);
        form.add(new Label("Instructions:"), 0, 3); form.add(fInstructions, 1, 3);

        VBox formCard = new VBox(form);
        formCard.getStyleClass().add("card");

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnPrescribe = new Button("Prescribe"); btnPrescribe.getStyleClass().addAll("btn", "btn-primary");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnPrescribe, btnClear);

        // Prescription history table for this doctor
        Label rxLabel = new Label("RECENT PRESCRIPTIONS BY YOU");
        rxLabel.getStyleClass().add("form-section");

        TableView<Prescription> rxTable = new TableView<>();
        rxTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rxTable.getStyleClass().add("table-view");
        TableColumn<Prescription, String> colPId = new TableColumn<>("Patient ID");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Prescription, String> colMed = new TableColumn<>("Medicine");
        colMed.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<Prescription, String> colDos = new TableColumn<>("Dosage");
        colDos.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        TableColumn<Prescription, String> colInst = new TableColumn<>("Instructions");
        colInst.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        TableColumn<Prescription, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePrescribed"));
        rxTable.getColumns().addAll(colPId, colMed, colDos, colInst, colDate);
        rxTable.setItems(FXCollections.observableArrayList(prescriptionController.getPrescriptionsByDoctor(currentDoctor.getDoctorId())));

        btnPrescribe.setOnAction(e -> {
            String pid = fPatientId.getText().trim();
            String med = fMedicine.getText().trim();
            String dose = fDosage.getText().trim();
            String inst = fInstructions.getText().trim();
            if (pid.isEmpty() || med.isEmpty() || dose.isEmpty() || inst.isEmpty()) {
                AlertUtil.showError("Error", "Fill all fields.");
                return;
            }
            if (patientController.getPatientById(pid) == null) {
                AlertUtil.showError("Error", "Patient ID not found.");
                return;
            }
            prescriptionController.addPrescription(pid, currentDoctor.getDoctorId(), med, dose, inst);
            AlertUtil.showInfo("Success", "Prescription added.");
            fMedicine.clear(); fDosage.clear(); fInstructions.clear();
            rxTable.setItems(FXCollections.observableArrayList(prescriptionController.getPrescriptionsByDoctor(currentDoctor.getDoctorId())));
        });

        btnClear.setOnAction(e -> { fPatientId.clear(); fMedicine.clear(); fDosage.clear(); fInstructions.clear(); });

        view.getChildren().addAll(title, formCard, btns, rxLabel, rxTable);
        contentArea.getChildren().setAll(view);
    }
}
