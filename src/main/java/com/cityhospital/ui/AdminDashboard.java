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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * AdminDashboard provides the full administrative interface.
 * 
 * ROLE-BASED ACCESS CONTROL:
 * Only users with ADMIN role can access this dashboard.
 */
public class AdminDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private StackPane contentArea;

    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final UserController userController = new UserController();
    private final FeedbackController feedbackController = new FeedbackController();

    public AdminDashboard(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = new BorderPane();
        buildDashboard();
    }

    private void buildDashboard() {
        // Top Banner
        VBox topBanner = new VBox(3);
        topBanner.getStyleClass().add("top-banner");
        Label title = new Label("THE CITY HOSPITAL  \u2014  Admin Dashboard");
        title.getStyleClass().add("top-banner-title");
        Label userLabel = new Label("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.getStyleClass().add("top-banner-subtitle");
        topBanner.getChildren().addAll(title, userLabel);

        // Sidebar
        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Button btnDashboard = createSidebarButton("\uD83C\uDFE0  Dashboard");
        Button btnPatients = createSidebarButton("\uD83D\uDC65  Manage Patients");
        Button btnDoctors = createSidebarButton("\uD83D\uDC69\u200D\u2695\uFE0F  Manage Doctors");
        Button btnReceptionists = createSidebarButton("\uD83D\uDC64  Manage Receptionists");
        Button btnAppointments = createSidebarButton("\uD83D\uDCC5  Manage Appointments");
        Button btnBilling = createSidebarButton("\uD83D\uDCB0  Billing");
        Button btnPharmacy = createSidebarButton("\uD83D\uDC8A  Pharmacy");
        Button btnStatistics = createSidebarButton("\uD83D\uDCCA  Statistics");
        Button btnNotifications = createSidebarButton("\uD83D\uDD14  Notification History");
        Button btnEmailConfig = createSidebarButton("\u2709\uFE0F  Email Settings");
        Button btnFeedback = createSidebarButton("\uD83D\uDCAC  Feedback");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("\uD83D\uDEAA  Logout");
        btnLogout.getStyleClass().add("sidebar-button-danger");

        sidebar.getChildren().addAll(btnDashboard, btnPatients, btnDoctors, btnReceptionists,
                                     btnAppointments, btnBilling, btnPharmacy, btnStatistics,
                                     btnNotifications, btnEmailConfig, btnFeedback, spacer, btnLogout);

        // Content Area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setTop(topBanner);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        btnDashboard.setOnAction(e -> showDashboard());
        btnPatients.setOnAction(e -> showPatientManagement());
        btnDoctors.setOnAction(e -> showDoctorManagement());
        btnReceptionists.setOnAction(e -> showReceptionistManagement());
        btnAppointments.setOnAction(e -> showAppointmentManagement());
        btnBilling.setOnAction(e -> showBilling());
        btnPharmacy.setOnAction(e -> showPharmacy());
        btnStatistics.setOnAction(e -> showStatistics());
        btnNotifications.setOnAction(e -> showNotificationHistory());
        btnEmailConfig.setOnAction(e -> showEmailConfig());
        btnFeedback.setOnAction(e -> showFeedbackView());
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

    // ===== DASHBOARD HOME =====
    private void showDashboard() {
        VBox dash = new VBox(20);
        dash.setAlignment(Pos.TOP_CENTER);
        dash.setPadding(new Insets(30, 0, 0, 0));

        Label welcome = new Label("Welcome, " + currentUser.getUsername() + "!");
        welcome.getStyleClass().add("welcome-title");
        Label desc = new Label("Use the sidebar to navigate to different management sections.");
        desc.getStyleClass().add("welcome-subtitle");

        int patientCount = patientController.getAllPatients().size();
        int doctorCount = doctorController.getAllDoctors().size();
        int apptCount = appointmentController.getAllAppointments().size();
        long pendingCount = appointmentController.getAllAppointments().stream()
            .filter(a -> Appointment.STATUS_PENDING.equals(a.getStatus())).count();

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(20, 0, 0, 0));
        statsRow.getChildren().addAll(
            createStatCard("👥", String.valueOf(patientCount), "Total Patients"),
            createStatCard("👨‍⚕️", String.valueOf(doctorCount), "Total Doctors"),
            createStatCard("📅", String.valueOf(apptCount), "Appointments"),
            createStatCard("⏳", String.valueOf(pendingCount), "Pending Approval")
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
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        return card;
    }

    // ===== PATIENT MANAGEMENT =====
    private void showPatientManagement() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("PATIENT MANAGEMENT");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by name...");
        Button btnSearch = new Button("Search");
        btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear");
        btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

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
        refreshPatientTable(table);

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
        for (var node : form.lookupAll(".label")) {
            if (node instanceof Label l) l.getStyleClass().add("form-label");
        }

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Patient"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearForm = new Button("Clear"); btnClearForm.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClearForm);

        view.getChildren().addAll(title, searchBar, table, form, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(patientController.searchPatients(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshPatientTable(table); });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fName.setText(sel.getName()); fPhone.setText(sel.getPhone());
                fEmail.setText(sel.getEmail()); fAge.setText(String.valueOf(sel.getAge()));
                fGender.setValue(sel.getGender()); fBlood.setValue(sel.getBloodGroup()); fHistory.setText(sel.getMedicalHistory());
            }
        });

        btnAdd.setOnAction(e -> {
            patientController.addPatient("", fName.getText(), fPhone.getText(), fEmail.getText(),
                                         fAge.getText(), fGender.getValue(), fHistory.getText(),
                                         fBlood.getValue());
            refreshPatientTable(table); clearPatientForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory);
        });

        btnUpdate.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if (!ValidationService.isValidName(fName.getText())) {
                    AlertUtil.showError("Error", "Invalid name."); return;
                }
                sel.setName(fName.getText()); sel.setPhone(fPhone.getText());
                sel.setEmail(fEmail.getText()); sel.setAge(ValidationService.parseAge(fAge.getText()));
                sel.setGender(fGender.getValue()); sel.setBloodGroup(fBlood.getValue()); sel.setMedicalHistory(fHistory.getText());
                patientController.updatePatient(sel); refreshPatientTable(table);
            } else AlertUtil.showError("Error", "Select a patient.");
        });

        btnDelete.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete this patient?")) {
                patientController.deletePatient(sel.getPatientId());
                refreshPatientTable(table); clearPatientForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory);
            }
        });

        btnClearForm.setOnAction(e -> clearPatientForm(fName, fPhone, fEmail, fAge, fGender, fBlood, fHistory));

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void refreshPatientTable(TableView<Patient> table) {
        table.setItems(FXCollections.observableArrayList(patientController.getAllPatients()));
    }

    private void clearPatientForm(TextField n, TextField p, TextField e, TextField a, ComboBox<String> g, ComboBox<String> b, TextField h) {
        n.clear(); p.clear(); e.clear(); a.clear(); g.setValue(null); b.setValue(null); h.clear();
    }

    // ===== DOCTOR MANAGEMENT =====
    private void showDoctorManagement() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("DOCTOR MANAGEMENT");
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
        TableColumn<Doctor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        table.getColumns().addAll(colId, colName, colSpec, colTime, colPhone, colEmail);
        refreshDoctorTable(table);

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));
        TextField fName = new TextField(); fName.setPromptText("Full Name"); fName.getStyleClass().add("text-field");
        TextField fPhone = new TextField(); fPhone.setPromptText("03XXXXXXXXX"); fPhone.getStyleClass().add("text-field");
        TextField fEmail = new TextField(); fEmail.setPromptText("email@example.com"); fEmail.getStyleClass().add("text-field");
        TextField fSpec = new TextField(); fSpec.setPromptText("Specialization"); fSpec.getStyleClass().add("text-field");
        TextField fTime = new TextField(); fTime.setPromptText("HH:mm (09:00-17:00)"); fTime.getStyleClass().add("text-field");
        form.add(new Label("Name:"), 0, 0); form.add(fName, 1, 0);
        form.add(new Label("Phone:"), 0, 1); form.add(fPhone, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(fEmail, 1, 2);
        form.add(new Label("Spec:"), 0, 3); form.add(fSpec, 1, 3);
        form.add(new Label("Time:"), 0, 4); form.add(fTime, 1, 4);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Doctor"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearForm = new Button("Clear"); btnClearForm.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClearForm);

        view.getChildren().addAll(title, searchBar, table, form, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(doctorController.searchDoctors(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshDoctorTable(table); });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fName.setText(sel.getName()); fPhone.setText(sel.getPhone());
                fEmail.setText(sel.getEmail()); fSpec.setText(sel.getSpecialization());
                fTime.setText(sel.getAvailableTime());
            }
        });

        btnAdd.setOnAction(e -> {
            doctorController.addDoctor("", fName.getText(), fPhone.getText(), fEmail.getText(), fSpec.getText(), fTime.getText());
            refreshDoctorTable(table); clearDoctorForm(fName, fPhone, fEmail, fSpec, fTime);
        });

        btnUpdate.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setName(fName.getText()); sel.setPhone(fPhone.getText()); sel.setEmail(fEmail.getText());
                sel.setSpecialization(fSpec.getText()); sel.setAvailableTime(fTime.getText());
                doctorController.updateDoctor(sel); refreshDoctorTable(table);
            } else AlertUtil.showError("Error", "Select a doctor.");
        });

        btnDelete.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete this doctor?")) {
                doctorController.deleteDoctor(sel.getDoctorId()); refreshDoctorTable(table);
                clearDoctorForm(fName, fPhone, fEmail, fSpec, fTime);
            }
        });

        btnClearForm.setOnAction(e -> clearDoctorForm(fName, fPhone, fEmail, fSpec, fTime));

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void refreshDoctorTable(TableView<Doctor> table) {
        table.setItems(FXCollections.observableArrayList(doctorController.getAllDoctors()));
    }

    private void clearDoctorForm(TextField n, TextField p, TextField e, TextField s, TextField t) {
        n.clear(); p.clear(); e.clear(); s.clear(); t.clear();
    }

    // ===== RECEPTIONIST MANAGEMENT =====
    private void showReceptionistManagement() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("RECEPTIONIST MANAGEMENT");
        title.getStyleClass().add("page-title");

        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<User, String> colId = new TableColumn<>("User ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        table.getColumns().addAll(colId, colUser, colRole);
        table.setItems(FXCollections.observableArrayList(userController.getReceptionists()));

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));
        TextField fUsername = new TextField(); fUsername.setPromptText("Username"); fUsername.getStyleClass().add("text-field");
        TextField fPassword = new TextField(); fPassword.setPromptText("Password"); fPassword.getStyleClass().add("text-field");
        form.add(new Label("Username:"), 0, 0); form.add(fUsername, 1, 0);
        form.add(new Label("Password:"), 0, 1); form.add(fPassword, 1, 1);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Receptionist"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        btns.getChildren().addAll(btnAdd, btnDelete);

        view.getChildren().addAll(title, table, form, btns);

        btnAdd.setOnAction(e -> {
            userController.addReceptionist(fUsername.getText(), fPassword.getText());
            table.setItems(FXCollections.observableArrayList(userController.getReceptionists()));
            fUsername.clear(); fPassword.clear();
        });

        btnDelete.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete this receptionist?")) {
                userController.deleteUser(sel.getUserId());
                table.setItems(FXCollections.observableArrayList(userController.getReceptionists()));
            }
        });

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    // ===== APPOINTMENT MANAGEMENT =====
    private void showAppointmentManagement() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("APPOINTMENT MANAGEMENT");
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
        TableColumn<Appointment, String> colAId = new TableColumn<>("Appt ID");
        colAId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient ID");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, String> colDId = new TableColumn<>("Doctor ID");
        colDId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colAId, colPId, colDId, colDate, colTime, colStatus);
        refreshAppointmentTable(table);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnCancel = new Button("Cancel Selected"); btnCancel.getStyleClass().addAll("btn", "btn-danger");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnCancel, btnRefresh);

        view.getChildren().addAll(title, searchBar, table, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(appointmentController.searchAppointments(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshAppointmentTable(table); });
        btnRefresh.setOnAction(e -> refreshAppointmentTable(table));

        btnCancel.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Cancel", "Cancel this appointment?")) {
                appointmentController.cancelAppointment(sel.getAppointmentId()); refreshAppointmentTable(table);
            }
        });

        contentArea.getChildren().setAll(wrapInScroll(view));
    }

    private void refreshAppointmentTable(TableView<Appointment> table) {
        table.setItems(FXCollections.observableArrayList(appointmentController.getAllAppointments()));
    }

    // ===== BILLING =====
    private void showBilling() {
        BillingView billingView = new BillingView();
        contentArea.getChildren().setAll(wrapInScroll(billingView.getView()));
    }

    // ===== PHARMACY =====
    private void showPharmacy() {
        PharmacyView pharmacyView = new PharmacyView();
        contentArea.getChildren().setAll(wrapInScroll(pharmacyView.getView()));
    }

    // ===== NOTIFICATION HISTORY =====
    private void showNotificationHistory() {
        NotificationHistoryView nhv = new NotificationHistoryView();
        contentArea.getChildren().setAll(wrapInScroll(nhv.getView()));
    }

    // ===== EMAIL CONFIG =====
    private void showEmailConfig() {
        EmailConfigView ecv = new EmailConfigView();
        contentArea.getChildren().setAll(wrapInScroll(ecv.getView()));
    }

    // ===== FEEDBACK =====
    private void showFeedbackView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("PATIENT FEEDBACK");
        title.getStyleClass().add("page-title");

        TableView<Feedback> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Feedback, String> colId = new TableColumn<>("Feedback ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        TableColumn<Feedback, String> colPName = new TableColumn<>("Patient");
        colPName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Feedback, Integer> colRating = new TableColumn<>("Rating");
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        TableColumn<Feedback, String> colComment = new TableColumn<>("Comment");
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        TableColumn<Feedback, String> colSubmitted = new TableColumn<>("Submitted");
        colSubmitted.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        TableColumn<Feedback, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colId, colPName, colRating, colComment, colSubmitted, colStatus);
        table.setItems(FXCollections.observableArrayList(feedbackController.getAllFeedback()));

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnResolve = new Button("Mark as Resolved"); btnResolve.getStyleClass().addAll("btn", "btn-success");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnResolve, btnRefresh);

        btnResolve.setOnAction(e -> {
            Feedback sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                feedbackController.markResolved(sel.getFeedbackId());
                table.setItems(FXCollections.observableArrayList(feedbackController.getAllFeedback()));
            } else AlertUtil.showError("Error", "Select feedback to resolve.");
        });
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(feedbackController.getAllFeedback())));

        view.getChildren().addAll(title, table, btns);
        contentArea.getChildren().setAll(view);
    }

    // ===== STATISTICS =====
    private void showStatistics() {
        StatisticsView statsView = new StatisticsView();
        contentArea.getChildren().setAll(statsView.getView());
    }

    private ScrollPane wrapInScroll(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }
}
