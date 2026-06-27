package com.cityhospital.ui;

import com.cityhospital.controller.*;
import com.cityhospital.model.*;
import com.cityhospital.service.NotificationService;
import com.cityhospital.service.PatientUserMapper;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.DateTimeUtil;
import com.cityhospital.MainApp;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

/**
 * PatientDashboard provides the patient's self-service interface.
 * ROLE-BASED ACCESS CONTROL: Only PATIENT role can access.
 */
public class PatientDashboard {

    private final Stage stage;
    private final User currentUser;
    private final BorderPane root;
    private StackPane contentArea;

    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final FeedbackController feedbackController = new FeedbackController();

    private Patient currentPatient;

    public PatientDashboard(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = new BorderPane();
        List<Patient> patients = patientController.getAllPatients();

        // 1. PatientUserMapper (properties file — no serialization issues)
        if (currentPatient == null && currentUser.getUsername() != null) {
            String mappedId = PatientUserMapper.getPatientId(currentUser.getUsername());
            if (mappedId != null) {
                currentPatient = patients.stream()
                        .filter(p -> mappedId.equals(p.getPatientId()))
                        .findFirst().orElse(null);
            }
        }

        // 2. Direct patientId lookup (User model field)
        if (currentPatient == null && currentUser.getPatientId() != null) {
            currentPatient = patients.stream()
                    .filter(p -> p.getPatientId().equals(currentUser.getPatientId()))
                    .findFirst().orElse(null);
        }

        // 3. Exact email match (User model field)
        if (currentPatient == null && currentUser.getPatientEmail() != null) {
            currentPatient = patients.stream()
                    .filter(p -> currentUser.getPatientEmail().equalsIgnoreCase(p.getEmail()))
                    .findFirst().orElse(null);
        }

        // 4. Fallback: email contains username (legacy demo user)
        if (currentPatient == null) {
            currentPatient = patients.stream()
                    .filter(p -> p.getEmail() != null && p.getEmail().contains(currentUser.getUsername()))
                    .findFirst().orElse(null);
        }

        if (currentPatient == null && !patients.isEmpty()) currentPatient = patients.get(0);
        buildDashboard();
    }

    private void buildDashboard() {
        VBox topBanner = new VBox(3);
        topBanner.getStyleClass().add("top-banner");
        Label title = new Label("THE CITY HOSPITAL  \u2014  Patient Portal");
        title.getStyleClass().add("top-banner-title");
        String name = currentPatient != null ? currentPatient.getName() : currentUser.getUsername();
        Label userLabel = new Label("Welcome, " + name);
        userLabel.getStyleClass().add("top-banner-subtitle");
        topBanner.getChildren().addAll(title, userLabel);

        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Button btnDashboard = createSidebarButton("\uD83C\uDFE0  Dashboard");
        Button btnProfile = createSidebarButton("\uD83D\uDC64  My Profile");
        Button btnBook = createSidebarButton("\uD83D\uDCCB  Book Appointment");
        Button btnHistory = createSidebarButton("\uD83D\uDCC5  Appointment History");
        Button btnPrescriptions = createSidebarButton("\uD83D\uDC8A  My Prescriptions");
        Button btnFeedback = createSidebarButton("\uD83D\uDCAC  Feedback");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("\uD83D\uDEAA  Logout");
        btnLogout.getStyleClass().add("sidebar-button-danger");

        sidebar.getChildren().addAll(btnDashboard, btnProfile, btnBook, btnHistory, btnPrescriptions, btnFeedback, spacer, btnLogout);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setTop(topBanner);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        btnDashboard.setOnAction(e -> showDashboard());
        btnProfile.setOnAction(e -> showProfile());
        btnBook.setOnAction(e -> showBookAppointment());
        btnHistory.setOnAction(e -> showAppointmentHistory());
        btnPrescriptions.setOnAction(e -> showPrescriptions());
        btnFeedback.setOnAction(e -> showFeedback());
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

        String name = currentPatient != null ? currentPatient.getName() : currentUser.getUsername();
        Label welcome = new Label("Welcome, " + name + "!");
        welcome.getStyleClass().add("welcome-title");
        Label desc = new Label("View your profile, book appointments, and manage prescriptions.");
        desc.getStyleClass().add("welcome-subtitle");

        String patientId = currentPatient != null ? currentPatient.getPatientId() : "";
        long myAppts = appointmentController.getAppointmentsByPatient(patientId).size();
        long upcoming = appointmentController.getAppointmentsByPatient(patientId).stream()
            .filter(a -> Appointment.STATUS_SCHEDULED.equals(a.getStatus())).count();
        long prescriptions = prescriptionController.getPrescriptionsByPatient(patientId).size();

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(20, 0, 0, 0));
        statsRow.getChildren().addAll(
            createStatCard("📅", String.valueOf(myAppts), "Total Appointments"),
            createStatCard("📋", String.valueOf(upcoming), "Upcoming"),
            createStatCard("💊", String.valueOf(prescriptions), "Prescriptions")
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

        if (currentPatient == null) {
            Label np = new Label("No patient profile found. Please contact reception.");
            np.setTextFill(Color.valueOf("#E74C3C"));
            view.getChildren().addAll(title, np);
            contentArea.getChildren().setAll(view);
            return;
        }

        GridPane profileGrid = new GridPane();
        profileGrid.getStyleClass().add("profile-grid");
        profileGrid.add(new Label("Patient ID:"), 0, 0); profileGrid.add(new Label(currentPatient.getPatientId()), 1, 0);
        profileGrid.add(new Label("Name:"), 0, 1); profileGrid.add(new Label(currentPatient.getName()), 1, 1);
        profileGrid.add(new Label("Age:"), 0, 2); profileGrid.add(new Label(String.valueOf(currentPatient.getAge())), 1, 2);
        profileGrid.add(new Label("Gender:"), 0, 3); profileGrid.add(new Label(currentPatient.getGender()), 1, 3);
        profileGrid.add(new Label("Blood Group:"), 0, 4); profileGrid.add(new Label(currentPatient.getBloodGroup()), 1, 4);

        VBox profileCard = new VBox(profileGrid);
        profileCard.getStyleClass().add("card");

        // Edit contact section
        Label editTitle = new Label("EDIT CONTACT INFORMATION");
        editTitle.getStyleClass().add("form-section");

        GridPane editGrid = new GridPane();
        editGrid.getStyleClass().add("form-grid");
        TextField fPhone = new TextField(currentPatient.getPhone());
        fPhone.setPromptText("03XXXXXXXXX"); fPhone.getStyleClass().add("text-field");
        TextField fEmail = new TextField(currentPatient.getEmail());
        fEmail.setPromptText("email@example.com"); fEmail.getStyleClass().add("text-field");
        editGrid.add(new Label("Phone:"), 0, 0); editGrid.add(fPhone, 1, 0);
        editGrid.add(new Label("Email:"), 0, 1); editGrid.add(fEmail, 1, 1);

        Button btnSave = new Button("Save Changes");
        btnSave.getStyleClass().addAll("btn", "btn-primary");

        VBox editCard = new VBox(10, editTitle, editGrid, btnSave);
        editCard.getStyleClass().add("card");

        btnSave.setOnAction(e -> {
            if (!ValidationService.isValidPhone(fPhone.getText())) {
                AlertUtil.showError("Error", "Invalid phone number."); return;
            }
            if (!ValidationService.isValidEmail(fEmail.getText())) {
                AlertUtil.showError("Error", "Invalid email."); return;
            }
            currentPatient.setPhone(fPhone.getText().trim());
            currentPatient.setEmail(fEmail.getText().trim());
            patientController.updatePatient(currentPatient);
            NotificationService.sendNotification("Profile updated successfully.");
        });

        view.getChildren().addAll(title, profileCard, editCard);
        contentArea.getChildren().setAll(view);
    }

    private void showBookAppointment() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("BOOK APPOINTMENT");
        title.getStyleClass().add("page-title");

        if (currentPatient == null) {
            Label np = new Label("Please register at reception first.");
            np.setTextFill(Color.valueOf("#E74C3C"));
            view.getChildren().addAll(title, np);
            contentArea.getChildren().setAll(view);
            return;
        }

        Label patientInfo = new Label("\uD83D\uDC65  Patient: " + currentPatient.getPatientId() + " - " + currentPatient.getName());
        patientInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #2C3E50;");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        ComboBox<String> cmbDoctors = new ComboBox<>();
        List<Doctor> doctors = doctorController.getAllDoctors();
        for (Doctor d : doctors) cmbDoctors.getItems().add(d.getDoctorId() + " - Dr. " + d.getName() + " (" + d.getSpecialization() + ")");
        cmbDoctors.setPromptText("Select Doctor");
        cmbDoctors.setPrefWidth(400);
        cmbDoctors.getStyleClass().add("combo-box");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
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

        form.add(new Label("Doctor:"), 0, 0); form.add(cmbDoctors, 1, 0);
        form.add(new Label("Date:"), 0, 1); form.add(datePicker, 1, 1);
        form.add(new Label("Time:"), 0, 2); form.add(cmbTime, 1, 2);
        form.add(new Label("Priority:"), 0, 3); form.add(cmbUrgency, 1, 3);

        VBox formCard = new VBox(15, form);
        formCard.getStyleClass().add("card");

        Button btnBook = new Button("Book Appointment");
        btnBook.getStyleClass().addAll("btn", "btn-primary");
        btnBook.setPrefWidth(300);

        btnBook.setOnAction(e -> {
            String dSel = cmbDoctors.getValue();
            LocalDate date = datePicker.getValue();
            String time = cmbTime.getValue();
            String urgency = cmbUrgency.getValue();
            if (dSel == null) { AlertUtil.showError("Error", "Select a doctor."); return; }
            if (date == null) { AlertUtil.showError("Error", "Select a date."); return; }
            if (date.isBefore(LocalDate.now())) { AlertUtil.showError("Error", "Date cannot be in the past."); return; }
            if (time == null) { AlertUtil.showError("Error", "Select a time slot."); return; }
            appointmentController.bookAppointmentPending(currentPatient.getPatientId(), dSel.split(" - ")[0], date.toString(), time, urgency);
            cmbDoctors.setValue(null); datePicker.setValue(null); cmbTime.setValue(null);
            cmbTime.setPromptText("Select Time Slot (select doctor & date first)");
            cmbTime.setDisable(true);
            cmbUrgency.setValue(Appointment.URGENCY_NORMAL);
        });

        view.getChildren().addAll(title, patientInfo, formCard, btnBook);
        contentArea.getChildren().setAll(view);
    }

    private void showAppointmentHistory() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("MY APPOINTMENTS");
        title.getStyleClass().add("page-title");

        if (currentPatient == null) {
            Label np = new Label("No patient profile found.");
            np.setTextFill(Color.valueOf("#E74C3C"));
            view.getChildren().addAll(title, np);
            contentArea.getChildren().setAll(view);
            return;
        }

        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Appointment, String> colId = new TableColumn<>("Appt ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colDoc = new TableColumn<>("Doctor ID");
        colDoc.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colUrgency = new TableColumn<>("Priority");
        colUrgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colId, colDoc, colDate, colTime, colUrgency, colStatus);
        table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByPatient(currentPatient.getPatientId())));

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnCancel = new Button("Cancel Selected"); btnCancel.getStyleClass().addAll("btn", "btn-danger");
        Button btnReschedule = new Button("Reschedule"); btnReschedule.getStyleClass().addAll("btn", "btn-warning");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnReschedule, btnCancel, btnRefresh);

        btnCancel.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Cancel", "Cancel this appointment?")) {
                appointmentController.cancelAppointment(sel.getAppointmentId());
                table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByPatient(currentPatient.getPatientId())));
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
                    table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByPatient(currentPatient.getPatientId())));
                } else if (r == ButtonType.OK) {
                    AlertUtil.showError("Error", "Select both date and time.");
                }
            });
        });
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(appointmentController.getAppointmentsByPatient(currentPatient.getPatientId()))));

        view.getChildren().addAll(title, table, btns);
        contentArea.getChildren().setAll(view);
    }

    private void showPrescriptions() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("MY PRESCRIPTIONS");
        title.getStyleClass().add("page-title");

        if (currentPatient == null) {
            view.getChildren().addAll(title, new Label("No patient profile found."));
            contentArea.getChildren().setAll(view);
            return;
        }

        TableView<Prescription> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Prescription, String> colMed = new TableColumn<>("Medicine");
        colMed.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<Prescription, String> colDos = new TableColumn<>("Dosage");
        colDos.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        TableColumn<Prescription, String> colInst = new TableColumn<>("Instructions");
        colInst.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        TableColumn<Prescription, String> colDate = new TableColumn<>("Date Prescribed");
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePrescribed"));
        table.getColumns().addAll(colMed, colDos, colInst, colDate);
        table.setItems(FXCollections.observableArrayList(prescriptionController.getPrescriptionsByPatient(currentPatient.getPatientId())));

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(prescriptionController.getPrescriptionsByPatient(currentPatient.getPatientId()))));

        view.getChildren().addAll(title, table, btnRefresh);
        contentArea.getChildren().setAll(view);
    }

    private void showFeedback() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(10));

        Label title = new Label("SUBMIT FEEDBACK");
        title.getStyleClass().add("page-title");

        if (currentPatient == null) {
            view.getChildren().addAll(title, new Label("No patient profile found."));
            contentArea.getChildren().setAll(view);
            return;
        }

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        Label lblRating = new Label("Rating (1-5):");
        ComboBox<Integer> cmbRating = new ComboBox<>();
        cmbRating.getItems().addAll(1, 2, 3, 4, 5);
        cmbRating.setValue(5);
        cmbRating.getStyleClass().add("combo-box");

        TextArea taComment = new TextArea();
        taComment.setPromptText("Share your experience...");
        taComment.setPrefRowCount(4);
        taComment.setPrefWidth(400);
        taComment.getStyleClass().add("text-field");

        form.add(lblRating, 0, 0); form.add(cmbRating, 1, 0);
        form.add(new Label("Comment:"), 0, 1); form.add(taComment, 1, 1);

        VBox formCard = new VBox(form);
        formCard.getStyleClass().add("card");

        Button btnSubmit = new Button("Submit Feedback");
        btnSubmit.getStyleClass().addAll("btn", "btn-primary");

        // Previous feedback by this patient
        Label prevLabel = new Label("YOUR PREVIOUS FEEDBACK");
        prevLabel.getStyleClass().add("form-section");

        TableView<Feedback> fbTable = new TableView<>();
        fbTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fbTable.getStyleClass().add("table-view");
        fbTable.setPrefHeight(200);

        TableColumn<Feedback, Integer> colRating = new TableColumn<>("Rating");
        colRating.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getRating()));
        colRating.setPrefWidth(70);
        TableColumn<Feedback, String> colComment = new TableColumn<>("Comment");
        colComment.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getComment()));
        colComment.setPrefWidth(200);
        colComment.setCellFactory(col -> new TableCell<Feedback, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setGraphic(null);
                if (!empty) setTooltip(new Tooltip(item));
            }
        });
        TableColumn<Feedback, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getSubmittedAt().toString()));
        colDate.setPrefWidth(130);
        TableColumn<Feedback, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getStatus()));
        colStatus.setPrefWidth(90);

        fbTable.getColumns().addAll(colRating, colComment, colDate, colStatus);
        fbTable.setItems(FXCollections.observableArrayList(feedbackController.getFeedbackByPatient(currentPatient.getPatientId())));

        fbTable.setRowFactory(tv -> {
            TableRow<Feedback> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    Feedback f = row.getItem();
                    Alert detail = new Alert(Alert.AlertType.INFORMATION);
                    detail.setTitle("Feedback Detail");
                    detail.setHeaderText("Rating: " + f.getRating() + "/5");
                    detail.setContentText("Comment:\n" + f.getComment() + "\n\nDate: " + f.getSubmittedAt() + "\nStatus: " + f.getStatus());
                    detail.showAndWait();
                }
            });
            return row;
        });

        btnSubmit.setOnAction(e -> {
            String comment = taComment.getText().trim();
            if (comment.isEmpty()) { AlertUtil.showError("Error", "Please write a comment."); return; }
            feedbackController.submitFeedback(currentPatient.getPatientId(), currentPatient.getName(), cmbRating.getValue(), comment);
            AlertUtil.showInfo("Thank You", "Your feedback has been submitted.");
            cmbRating.setValue(5); taComment.clear();
            fbTable.setItems(FXCollections.observableArrayList(feedbackController.getFeedbackByPatient(currentPatient.getPatientId())));
        });

        view.getChildren().addAll(title, formCard, btnSubmit, prevLabel, fbTable);
        contentArea.getChildren().setAll(view);
    }
}
