package com.cityhospital.ui;

import com.cityhospital.controller.*;
import com.cityhospital.model.*;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.DateTimeUtil;
import com.cityhospital.MainApp;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

/**
 * ReceptionistDashboard provides the receptionist's interface.
 * ROLE-BASED ACCESS CONTROL: Only RECEPTIONIST role can access.
 */
public class ReceptionistDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private StackPane contentArea;

    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final EmergencyCaseController emergencyCaseController = new EmergencyCaseController();

    public ReceptionistDashboard(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = new BorderPane();
        buildDashboard();
    }

    private void buildDashboard() {
        VBox topBanner = new VBox(3);
        topBanner.getStyleClass().add("top-banner");
        Label title = new Label("THE CITY HOSPITAL  \u2014  Receptionist Dashboard");
        title.getStyleClass().add("top-banner-title");
        Label userLabel = new Label("Logged in as: " + currentUser.getUsername());
        userLabel.getStyleClass().add("top-banner-subtitle");
        topBanner.getChildren().addAll(title, userLabel);

        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Button btnDashboard = createSidebarButton("\uD83C\uDFE0  Dashboard");
        Button btnPatients = createSidebarButton("\uD83D\uDC65  Register Patient");
        Button btnPending = createSidebarButton("\u23F3  Pending Approvals");
        Button btnBook = createSidebarButton("\uD83D\uDCCB  Book Appointment");
        Button btnAppointments = createSidebarButton("\uD83D\uDCC5  View Appointments");
        Button btnBilling = createSidebarButton("\uD83D\uDCB0  Billing");
        Button btnNotifications = createSidebarButton("\uD83D\uDD14  Notification History");
        Button btnEmergency = createSidebarButton("\u26A1  Emergency Cases");
        Button btnDoctors = createSidebarButton("\uD83D\uDC69\u200D\u2695\uFE0F  View Doctors");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("\uD83D\uDEAA  Logout");
        btnLogout.getStyleClass().add("sidebar-button-danger");

        sidebar.getChildren().addAll(btnDashboard, btnPatients, btnPending, btnBook, btnAppointments, btnEmergency, btnBilling, btnNotifications, btnDoctors, spacer, btnLogout);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setTop(topBanner);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        btnDashboard.setOnAction(e -> showDashboard());
        btnPatients.setOnAction(e -> showPatientRegistration());
        btnPending.setOnAction(e -> showPendingApprovals());
        btnBook.setOnAction(e -> showBookAppointment());
        btnAppointments.setOnAction(e -> showAppointmentView());
        btnBilling.setOnAction(e -> showBilling());
        btnNotifications.setOnAction(e -> showNotificationHistory());
        btnEmergency.setOnAction(e -> showEmergencyCases());
        btnDoctors.setOnAction(e -> showDoctorsView());
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

        Label welcome = new Label("Welcome, " + currentUser.getUsername() + "!");
        welcome.getStyleClass().add("welcome-title");
        Label desc = new Label("Manage patients, appointments, and billing from the sidebar.");
        desc.getStyleClass().add("welcome-subtitle");

        int patientCount = patientController.getAllPatients().size();
        int doctorCount = doctorController.getAllDoctors().size();
        long pendingCount = appointmentController.getAllAppointments().stream()
            .filter(a -> Appointment.STATUS_PENDING.equals(a.getStatus())).count();
        long todayCount = appointmentController.getAllAppointments().stream()
            .filter(a -> LocalDate.now().toString().equals(String.valueOf(a.getAppointmentDate()))).count();

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(20, 0, 0, 0));
        statsRow.getChildren().addAll(
            createStatCard("👥", String.valueOf(patientCount), "Registered Patients"),
            createStatCard("👨‍⚕️", String.valueOf(doctorCount), "Available Doctors"),
            createStatCard("⏳", String.valueOf(pendingCount), "Pending Approvals"),
            createStatCard("📅", String.valueOf(todayCount), "Today's Appointments")
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

    private void showPatientRegistration() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("PATIENT REGISTRATION");
        title.getStyleClass().add("page-title");

        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Patient, String> colId = new TableColumn<>("Patient ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Patient, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Patient, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Patient, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Patient, Integer> colAge = new TableColumn<>("Age");
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Patient, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        TableColumn<Patient, String> colBlood = new TableColumn<>("Blood Group");
        colBlood.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
        table.getColumns().addAll(colId, colName, colPhone, colEmail, colAge, colGender, colBlood);
        table.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));

        TextField fName = new TextField(); fName.setPromptText("Full Name"); fName.getStyleClass().add("text-field");
        TextField fPhone = new TextField(); fPhone.setPromptText("03XXXXXXXXX"); fPhone.getStyleClass().add("text-field");
        TextField fEmail = new TextField(); fEmail.setPromptText("email@example.com"); fEmail.getStyleClass().add("text-field");
        TextField fAge = new TextField(); fAge.setPromptText("Age"); fAge.getStyleClass().add("text-field");
        ComboBox<String> fGender = new ComboBox<>();
        fGender.getItems().addAll("Male", "Female", "Other");
        fGender.setPromptText("Gender");
        fGender.getStyleClass().add("combo-box");
        ComboBox<String> fBlood = new ComboBox<>();
        fBlood.getItems().addAll(Patient.BLOOD_GROUPS);
        fBlood.setPromptText("Blood Group");
        fBlood.getStyleClass().add("combo-box");
        TextField fHistory = new TextField(); fHistory.setPromptText("Medical History"); fHistory.getStyleClass().add("text-field");

        form.add(new Label("Name:"), 0, 0); form.add(fName, 1, 0);
        form.add(new Label("Phone:"), 0, 1); form.add(fPhone, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(fEmail, 1, 2);
        form.add(new Label("Age:"), 0, 3); form.add(fAge, 1, 3);
        form.add(new Label("Gender:"), 0, 4); form.add(fGender, 1, 4);
        form.add(new Label("Blood Group:"), 0, 5); form.add(fBlood, 1, 5);
        form.add(new Label("History:"), 0, 6); form.add(fHistory, 1, 6);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnRegister = new Button("+ Register Patient"); btnRegister.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnRegister, btnUpdate, btnDelete, btnClear);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fName.setText(sel.getName()); fPhone.setText(sel.getPhone());
                fEmail.setText(sel.getEmail()); fAge.setText(String.valueOf(sel.getAge()));
                fGender.setValue(sel.getGender()); fBlood.setValue(sel.getBloodGroup()); fHistory.setText(sel.getMedicalHistory());
            }
        });

        btnRegister.setOnAction(e -> {
            patientController.addPatient("", fName.getText(), fPhone.getText(), fEmail.getText(),
                                         fAge.getText(), fGender.getValue(), fHistory.getText(),
                                         fBlood.getValue());
            table.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));
            clearForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory);
        });

        btnUpdate.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setName(fName.getText()); sel.setPhone(fPhone.getText());
                sel.setEmail(fEmail.getText()); sel.setAge(ValidationService.parseAge(fAge.getText()));
                sel.setGender(fGender.getValue()); sel.setBloodGroup(fBlood.getValue()); sel.setMedicalHistory(fHistory.getText());
                patientController.updatePatient(sel);
                table.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));
            } else AlertUtil.showError("Error", "Select a patient.");
        });

        btnDelete.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete this patient?")) {
                patientController.deletePatient(sel.getPatientId());
                table.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));
                clearForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory);
            }
        });

        btnClear.setOnAction(e -> clearForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory));

        view.getChildren().addAll(title, table, form, btns);
        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void clearForm(TextField n, TextField p, TextField e, TextField a, ComboBox<String> g, ComboBox<String> b, TextField h) {
        n.clear(); p.clear(); e.clear(); a.clear(); g.setValue(null); b.setValue(null); h.clear();
    }

    // ===== PENDING APPROVALS =====
    private void showPendingApprovals() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("PENDING APPOINTMENT APPROVALS");
        title.getStyleClass().add("page-title");

        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Appointment, String> colId = new TableColumn<>("Appt ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, String> colDId = new TableColumn<>("Doctor");
        colDId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colUrgency = new TableColumn<>("Priority");
        colUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colId, colPId, colDId, colDate, colTime, colUrgency, colStatus);

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

        refreshPendingTable(table);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAccept = new Button("\u2714  Accept"); btnAccept.getStyleClass().addAll("btn", "btn-success");
        Button btnReject = new Button("\u2718  Reject"); btnReject.getStyleClass().addAll("btn", "btn-danger");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAccept, btnReject, btnRefresh);

        view.getChildren().addAll(title, table, btns);

        btnAccept.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select a pending appointment."); return; }
            if (appointmentController.approveAppointment(sel.getAppointmentId())) {
                refreshPendingTable(table);
            }
        });

        btnReject.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select a pending appointment."); return; }
            if (AlertUtil.showConfirmation("Reject", "Reject this appointment request?")) {
                appointmentController.rejectAppointment(sel.getAppointmentId());
                refreshPendingTable(table);
            }
        });

        btnRefresh.setOnAction(e -> refreshPendingTable(table));

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void refreshPendingTable(TableView<Appointment> table) {
        table.setItems(FXCollections.observableArrayList(
            appointmentController.getAllAppointments().stream()
                .filter(a -> Appointment.STATUS_PENDING.equals(a.getStatus()))
                .sorted((a, b) -> Integer.compare(
                        Appointment.urgencyPriority(b.getUrgency()),
                        Appointment.urgencyPriority(a.getUrgency())))
                .toList()
        ));
    }

    private void showBookAppointment() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("BOOK APPOINTMENT");
        title.getStyleClass().add("page-title");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        ComboBox<String> cmbPatients = new ComboBox<>();
        List<Patient> patients = patientController.getAllPatients();
        for (Patient p : patients) cmbPatients.getItems().add(p.getPatientId() + " - " + p.getName());
        cmbPatients.setPromptText("Select Patient");
        cmbPatients.setPrefWidth(400);
        cmbPatients.getStyleClass().add("combo-box");

        ComboBox<String> cmbDoctors = new ComboBox<>();
        List<Doctor> doctors = doctorController.getAllDoctors();
        for (Doctor d : doctors) cmbDoctors.getItems().add(d.getDoctorId() + " - Dr. " + d.getName() + " (" + d.getSpecialization() + ")");
        cmbDoctors.setPromptText("Select Doctor");
        cmbDoctors.setPrefWidth(400);
        cmbDoctors.getStyleClass().add("combo-box");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setPrefWidth(400);
        datePicker.getStyleClass().add("date-picker");

        ComboBox<String> cmbTime = new ComboBox<>();
        cmbTime.setPromptText("Select Time Slot (select doctor & date first)");
        cmbTime.setPrefWidth(200);
        cmbTime.setEditable(false);
        cmbTime.getStyleClass().add("combo-box");
        cmbTime.setDisable(true);

        ComboBox<String> cmbUrgency = new ComboBox<>();
        cmbUrgency.getItems().addAll(Appointment.URGENCY_NORMAL, Appointment.URGENCY_URGENT, Appointment.URGENCY_EMERGENCY);
        cmbUrgency.setValue(Appointment.URGENCY_NORMAL);
        cmbUrgency.setPrefWidth(200);
        cmbUrgency.getStyleClass().add("combo-box");

        Runnable refreshSlots = () -> {
            String dSel = cmbDoctors.getValue();
            LocalDate date = datePicker.getValue();
            cmbTime.getItems().clear();
            if (dSel != null && date != null && !date.isBefore(LocalDate.now())) {
                String doctorId = dSel.split(" - ")[0];
                List<String> available = appointmentController.getAvailableSlots(doctorId, date.toString());
                if (available.isEmpty()) {
                    cmbTime.setPromptText("No slots available");
                } else {
                    cmbTime.getItems().addAll(available);
                    cmbTime.setPromptText("Select Time Slot");
                }
                cmbTime.setDisable(false);
            } else {
                cmbTime.setDisable(true);
                cmbTime.setPromptText("Select doctor & date first");
            }
            cmbTime.setValue(null);
        };
        cmbDoctors.valueProperty().addListener((o, a, b) -> refreshSlots.run());
        datePicker.valueProperty().addListener((o, a, b) -> refreshSlots.run());

        form.add(new Label("Patient:"), 0, 0); form.add(cmbPatients, 1, 0);
        form.add(new Label("Doctor:"), 0, 1); form.add(cmbDoctors, 1, 1);
        form.add(new Label("Date:"), 0, 2); form.add(datePicker, 1, 2);
        form.add(new Label("Time:"), 0, 3); form.add(cmbTime, 1, 3);
        form.add(new Label("Priority:"), 0, 4); form.add(cmbUrgency, 1, 4);

        VBox formCard = new VBox(15, form);
        formCard.getStyleClass().add("card");

        Button btnBook = new Button("Book Appointment");
        btnBook.getStyleClass().addAll("btn", "btn-primary");
        btnBook.setPrefWidth(300);

        btnBook.setOnAction(e -> {
            String pSel = cmbPatients.getValue();
            String dSel = cmbDoctors.getValue();
            LocalDate date = datePicker.getValue();
            String time = cmbTime.getValue();
            String urgency = cmbUrgency.getValue();

            if (pSel == null) { AlertUtil.showError("Error", "Select a patient."); return; }
            if (dSel == null) { AlertUtil.showError("Error", "Select a doctor."); return; }
            if (date == null) { AlertUtil.showError("Error", "Select a date."); return; }
            if (date.isBefore(LocalDate.now())) { AlertUtil.showError("Error", "Date cannot be in the past."); return; }
            if (time == null) { AlertUtil.showError("Error", "Select a time slot."); return; }

            appointmentController.bookAppointment(pSel.split(" - ")[0], dSel.split(" - ")[0], date.toString(), time, urgency);
            cmbPatients.setValue(null); cmbDoctors.setValue(null); datePicker.setValue(null);
            cmbTime.setValue(null); cmbTime.setPromptText("Select Time Slot (select doctor & date first)");
            cmbTime.setDisable(true);
            cmbUrgency.setValue(Appointment.URGENCY_NORMAL);
        });

        view.getChildren().addAll(title, formCard, btnBook);
        contentArea.getChildren().setAll(view);
    }

    private void showAppointmentView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("ALL APPOINTMENTS");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by ID...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Appointment, String> colId = new TableColumn<>("Appt ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, String> colDId = new TableColumn<>("Doctor");
        colDId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colUrgency = new TableColumn<>("Priority");
        colUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colId, colPId, colDId, colDate, colTime, colUrgency, colStatus);

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

        table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments()));

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnCancel = new Button("Cancel Appointment"); btnCancel.getStyleClass().addAll("btn", "btn-danger");
        Button btnReschedule = new Button("Reschedule"); btnReschedule.getStyleClass().addAll("btn", "btn-warning");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnReschedule, btnCancel, btnRefresh);

        view.getChildren().addAll(title, searchBar, table, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(appointmentController.searchAppointments(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments())); });
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments())));
        btnCancel.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Cancel", "Cancel this appointment?")) {
                appointmentController.cancelAppointment(sel.getAppointmentId());
                table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments()));
            }
        });
        btnReschedule.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select an appointment to reschedule."); return; }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Reschedule Appointment");
            dialog.setHeaderText("Appointment: " + sel.getAppointmentId());
            DialogPane pane = dialog.getDialogPane();
            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10);
            DatePicker newDate = new DatePicker();
            ComboBox<String> newTime = new ComboBox<>();
            newTime.getItems().addAll(DateTimeUtil.getTimeSlots());
            grid.add(new Label("New Date:"), 0, 0); grid.add(newDate, 1, 0);
            grid.add(new Label("New Time:"), 0, 1); grid.add(newTime, 1, 1);
            pane.setContent(grid);
            pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK && newDate.getValue() != null && newTime.getValue() != null) {
                    appointmentController.rescheduleAppointment(sel.getAppointmentId(), newDate.getValue().toString(), newTime.getValue());
                    table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments()));
                } else if (r == ButtonType.OK) {
                    AlertUtil.showError("Error", "Select both date and time.");
                }
            });
        });

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void showEmergencyCases() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("EMERGENCY CASES");
        title.getStyleClass().add("page-title");

        TableView<EmergencyCase> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<EmergencyCase, String> colId = new TableColumn<>("Case ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("caseId"));
        TableColumn<EmergencyCase, String> colPId = new TableColumn<>("Patient");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<EmergencyCase, String> colDoc = new TableColumn<>("Doctor");
        colDoc.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<EmergencyCase, String> colUrgency = new TableColumn<>("Urgency");
        colUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        TableColumn<EmergencyCase, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<EmergencyCase, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        table.getColumns().addAll(colId, colPId, colDoc, colUrgency, colStatus, colDesc);
        table.setItems(FXCollections.observableArrayList(emergencyCaseController.getOpenCases()));

        table.setRowFactory(tv -> new TableRow<EmergencyCase>() {
            @Override
            protected void updateItem(EmergencyCase item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); }
                else if (EmergencyCase.URGENCY_EMERGENCY.equals(item.getUrgency())) {
                    setStyle("-fx-background-color: #FFE0E0;");
                } else if (EmergencyCase.URGENCY_URGENT.equals(item.getUrgency())) {
                    setStyle("-fx-background-color: #FFF3CD;");
                } else { setStyle(""); }
            }
        });

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnMarkProgress = new Button("Mark In Progress"); btnMarkProgress.getStyleClass().addAll("btn", "btn-warning");
        Button btnMarkTreated = new Button("Mark Treated"); btnMarkTreated.getStyleClass().addAll("btn", "btn-success");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnMarkProgress, btnMarkTreated, btnRefresh);

        btnMarkProgress.setOnAction(e -> {
            EmergencyCase sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select a case."); return; }
            emergencyCaseController.updateStatus(sel.getCaseId(), EmergencyCase.STATUS_IN_PROGRESS);
            table.setItems(FXCollections.observableArrayList(emergencyCaseController.getOpenCases()));
        });
        btnMarkTreated.setOnAction(e -> {
            EmergencyCase sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select a case."); return; }
            emergencyCaseController.updateStatus(sel.getCaseId(), EmergencyCase.STATUS_TREATED);
            table.setItems(FXCollections.observableArrayList(emergencyCaseController.getOpenCases()));
        });
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(emergencyCaseController.getOpenCases())));

        view.getChildren().addAll(title, table, btns);
        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void showDoctorsView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("DOCTORS LIST");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by name...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        TableView<Doctor> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Doctor, String> colId = new TableColumn<>("Doctor ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Doctor, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, String> colSpec = new TableColumn<>("Specialization");
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Doctor, String> colTime = new TableColumn<>("Available");
        colTime.setCellValueFactory(new PropertyValueFactory<>("availableTime"));
        TableColumn<Doctor, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table.getColumns().addAll(colId, colName, colSpec, colTime, colPhone);
        table.setItems(FXCollections.observableArrayList(doctorController.getAllDoctors()));

        view.getChildren().addAll(title, searchBar, table);
        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(doctorController.searchDoctors(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); table.setItems(FXCollections.observableArrayList(doctorController.getAllDoctors())); });

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    // ===== BILLING =====
    private void showBilling() {
        BillingView billingView = new BillingView();
        contentArea.getChildren().setAll(wrapInScroll(billingView.getView()));
    }

    // ===== NOTIFICATION HISTORY =====
    private void showNotificationHistory() {
        NotificationHistoryView nhv = new NotificationHistoryView();
        contentArea.getChildren().setAll(wrapInScroll(nhv.getView()));
    }

    private ScrollPane wrapInScroll(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return sp;
    }
}
