package com.cityhospital.ui;

import com.cityhospital.controller.LoginController;
import com.cityhospital.model.Patient;
import com.cityhospital.model.User;
import com.cityhospital.service.AuthenticationService;
import com.cityhospital.service.FileManager;
import com.cityhospital.service.PatientUserMapper;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.SVGPath;

/**
 * LoginView creates the login interface for THE CITY HOSPITAL system.
 * 
 * This is the first screen the user sees when launching the application.
 * It provides:
 * - Username text field
 * - Password field (masked input)
 * - Login button
 * - Visual branding with hospital name and modern styling
 * 
 * After successful authentication, the user is directed to their
 * role-appropriate dashboard.
 */
public class LoginView {

    private final LoginController loginController;
    private final Stage primaryStage;
    private Scene loginScene;

    /** Login callback interface for role-based navigation */
    public interface LoginCallback {
        void onLoginSuccess(User user);
    }

    private LoginCallback callback;

    /**
     * Constructor.
     * 
     * @param primaryStage The primary stage of the application
     */
    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.loginController = new LoginController();
    }

    /**
     * Sets the callback to be invoked after successful login.
     * 
     * @param callback The login success callback
     */
    public void setLoginCallback(LoginCallback callback) {
        this.callback = callback;
    }

    /**
     * Creates and returns the login scene.
     * 
     * @return The configured login Scene
     */
    public Scene getScene() {
        if (loginScene == null) {
            loginScene = createLoginScene();
        }
        return loginScene;
    }

    /**
     * Builds the login UI layout with modern styling.
     * Layout structure:
     * - Split layout with left banner and right login card
     * - Left: Hospital branding
     * - Right: Login form with card-style container
     */
    private Scene createLoginScene() {
        // Root layout using HBox for side-by-side layout
        HBox root = new HBox();
        root.getStyleClass().add("login-root");

        // ===== LEFT BANNER =====
        VBox leftBanner = new VBox(15);
        leftBanner.setAlignment(Pos.CENTER);
        leftBanner.setPrefWidth(450);
        leftBanner.getStyleClass().add("login-banner");

        // Heart with EKG icon
        Group icon = new Group();
        SVGPath heart = new SVGPath();
        heart.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
        heart.setFill(Color.WHITE);
        heart.setScaleX(2.5);
        heart.setScaleY(2.5);
        Polyline ekg = new Polyline();
        ekg.getPoints().addAll(
            4.0, 16.0, 6.0, 16.0, 7.0, 14.0, 8.0, 18.0,
            9.0, 12.0, 10.0, 16.0, 11.0, 10.0, 12.0, 22.0,
            13.0, 6.0, 14.0, 22.0, 15.0, 10.0, 16.0, 16.0,
            17.0, 12.0, 18.0, 18.0, 19.0, 14.0, 20.0, 16.0,
            22.0, 16.0
        );
        ekg.setStroke(Color.rgb(255, 80, 80));
        ekg.setStrokeWidth(2.0);
        ekg.setStrokeLineCap(StrokeLineCap.ROUND);
        ekg.setStrokeLineJoin(StrokeLineJoin.ROUND);
        ekg.setScaleX(2.5);
        ekg.setScaleY(2.5);
        icon.getChildren().addAll(heart, ekg);

        Label hospitalName = new Label("THE CITY\nHOSPITAL");
        hospitalName.setAlignment(Pos.CENTER);
        hospitalName.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        Label tagline = new Label("Smart Healthcare Appointment\n& Patient Management System");
        tagline.setAlignment(Pos.CENTER);
        tagline.setLineSpacing(4);
        tagline.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.85); -fx-font-family: 'Segoe UI';");

        Separator sep = new Separator();
        sep.setMaxWidth(80);
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.4);");

        Label trust = new Label("Providing Quality Healthcare Since 2026");
        trust.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.6); -fx-font-family: 'Segoe UI';");

        leftBanner.getChildren().addAll(icon, hospitalName, sep, tagline, trust);

        // ===== RIGHT LOGIN FORM =====
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPrefWidth(550);
        rightPanel.setPadding(new Insets(40));

        // Login card container
        VBox loginCard = new VBox(20);
        loginCard.getStyleClass().add("login-card");

        // Card header
        VBox header = new VBox(5);
        Label loginTitle = new Label("Welcome Back");
        loginTitle.getStyleClass().add("login-title");
        Label loginSub = new Label("Sign in to access your dashboard");
        loginSub.getStyleClass().add("login-subtitle");
        header.getChildren().addAll(loginTitle, loginSub);

        // Spacer
        Region spacer1 = new Region();
        spacer1.setPrefHeight(10);

        // Role selector
        Label lblRole = new Label("ROLE");
        lblRole.getStyleClass().add("form-label");
        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT");
        cmbRole.setValue("PATIENT");
        cmbRole.setPromptText("Select your role");
        cmbRole.getStyleClass().add("combo-box");
        cmbRole.setPrefHeight(42);
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        // Username field with icon prefix
        Label lblUsername = new Label("USERNAME");
        lblUsername.getStyleClass().add("form-label");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");
        txtUsername.getStyleClass().add("text-field");
        txtUsername.setPrefHeight(42);
        txtUsername.setMaxWidth(Double.MAX_VALUE);

        // Password field
        Label lblPassword = new Label("PASSWORD");
        lblPassword.getStyleClass().add("form-label");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.getStyleClass().add("password-field");
        txtPassword.setPrefHeight(42);
        txtPassword.setMaxWidth(Double.MAX_VALUE);

        // Spacer
        Region spacer2 = new Region();
        spacer2.setPrefHeight(5);

        // Login button (full width)
        Button btnLogin = new Button("SIGN IN");
        btnLogin.getStyleClass().add("login-button");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(46);

        // Error label
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
        lblError.setVisible(false);
        lblError.setManaged(false);

        // Register link
        Hyperlink registerLink = new Hyperlink("New patient? Register here");
        registerLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #8B0000;");
        registerLink.setAlignment(Pos.CENTER);
        registerLink.setMaxWidth(Double.MAX_VALUE);

        loginCard.getChildren().addAll(header, spacer1,
                lblRole, cmbRole,
                lblUsername, txtUsername,
                lblPassword, txtPassword,
                spacer2, btnLogin, lblError, registerLink);

        rightPanel.getChildren().add(loginCard);

        // ===== REGISTRATION FORM =====
        VBox regCard = new VBox(15);
        regCard.getStyleClass().add("login-card");
        regCard.setVisible(false);
        regCard.setManaged(false);

        VBox regHeader = new VBox(5);
        Label regTitle = new Label("Patient Registration");
        regTitle.getStyleClass().add("login-title");
        Label regSub = new Label("Create your account to book appointments online");
        regSub.getStyleClass().add("login-subtitle");
        regHeader.getChildren().addAll(regTitle, regSub);

        TextField regName = new TextField(); regName.setPromptText("Full Name"); regName.getStyleClass().add("text-field");
        TextField regPhone = new TextField(); regPhone.setPromptText("03XXXXXXXXX"); regPhone.getStyleClass().add("text-field");
        TextField regEmail = new TextField(); regEmail.setPromptText("email@example.com"); regEmail.getStyleClass().add("text-field");
        TextField regAge = new TextField(); regAge.setPromptText("Age"); regAge.getStyleClass().add("text-field");
        ComboBox<String> regGender = new ComboBox<>(); regGender.getItems().addAll("Male", "Female", "Other"); regGender.setPromptText("Gender"); regGender.getStyleClass().add("combo-box");
        ComboBox<String> regBlood = new ComboBox<>(); regBlood.getItems().addAll(Patient.BLOOD_GROUPS); regBlood.setPromptText("Blood Group"); regBlood.getStyleClass().add("combo-box");
        TextField regUser = new TextField(); regUser.setPromptText("Desired Username"); regUser.getStyleClass().add("text-field");
        PasswordField regPass = new PasswordField(); regPass.setPromptText("Password"); regPass.getStyleClass().add("password-field");
        PasswordField regConfirm = new PasswordField(); regConfirm.setPromptText("Confirm Password"); regConfirm.getStyleClass().add("password-field");

        Button btnRegister = new Button("CREATE ACCOUNT");
        btnRegister.getStyleClass().add("login-button");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setPrefHeight(46);

        Label regError = new Label();
        regError.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
        regError.setVisible(false); regError.setManaged(false);

        Hyperlink backLink = new Hyperlink("\u2190  Back to Login");
        backLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #8B0000;");
        backLink.setAlignment(Pos.CENTER);
        backLink.setMaxWidth(Double.MAX_VALUE);

        regCard.getChildren().addAll(regHeader, regName, regPhone, regEmail, regAge, regGender, regBlood,
                                     regUser, regPass, regConfirm, btnRegister, regError, backLink);

        rightPanel.getChildren().add(regCard);

        // Add both halves to root
        root.getChildren().addAll(leftBanner, rightPanel);

        // ===== TOGGLE LOGIN / REGISTRATION =====
        Runnable showLoginForm = () -> { loginCard.setVisible(true); loginCard.setManaged(true); regCard.setVisible(false); regCard.setManaged(false); regError.setVisible(false); };
        Runnable showRegForm = () -> { loginCard.setVisible(false); loginCard.setManaged(false); regCard.setVisible(true); regCard.setManaged(true); regError.setVisible(false); };

        registerLink.setOnAction(e -> showRegForm.run());
        backLink.setOnAction(e -> showLoginForm.run());

        // ===== LOGIN EVENT HANDLING =====
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String role = cmbRole.getValue();

            boolean success = loginController.login(username, password, role);
            if (success) {
                User loggedInUser = loginController.getLoggedInUser();
                if (callback != null) {
                    callback.onLoginSuccess(loggedInUser);
                }
            } else {
                lblError.setText("Invalid username or password. Please try again.");
                lblError.setVisible(true);
                lblError.setManaged(true);
                txtUsername.setStyle(txtUsername.getStyle() + "-fx-border-color: #E74C3C;");
                txtPassword.setStyle(txtPassword.getStyle() + "-fx-border-color: #E74C3C;");
            }
        });

        txtPassword.setOnAction(e -> btnLogin.fire());

        txtUsername.textProperty().addListener((obs, old, val) -> {
            lblError.setVisible(false); lblError.setManaged(false); txtUsername.setStyle(""); txtPassword.setStyle("");
        });
        txtPassword.textProperty().addListener((obs, old, val) -> {
            lblError.setVisible(false); lblError.setManaged(false); txtUsername.setStyle(""); txtPassword.setStyle("");
        });

        // ===== REGISTRATION EVENT HANDLING =====
        btnRegister.setOnAction(e -> {
            String name = regName.getText().trim();
            String phone = regPhone.getText().trim();
            String email = regEmail.getText().trim();
            String ageStr = regAge.getText().trim();
            String gender = regGender.getValue();
            String blood = regBlood.getValue();
            String username = regUser.getText().trim();
            String password = regPass.getText().trim();
            String confirm = regConfirm.getText().trim();

            // Clear previous error
            regError.setVisible(false); regError.setManaged(false);

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || ageStr.isEmpty() || gender == null
                    || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                regError.setText("Please fill in all fields."); regError.setVisible(true); regError.setManaged(true); return;
            }
            if (!password.equals(confirm)) {
                regError.setText("Passwords do not match."); regError.setVisible(true); regError.setManaged(true); return;
            }
            if (!ValidationService.isValidName(name)) {
                regError.setText("Invalid name (2-50 characters)."); regError.setVisible(true); regError.setManaged(true); return;
            }
            if (!ValidationService.isValidPhone(phone)) {
                regError.setText("Invalid phone (11 digits starting with 03)."); regError.setVisible(true); regError.setManaged(true); return;
            }
            if (!ValidationService.isValidEmail(email)) {
                regError.setText("Invalid email address."); regError.setVisible(true); regError.setManaged(true); return;
            }
            if (!ValidationService.isValidAge(ageStr)) {
                regError.setText("Invalid age (0-120)."); regError.setVisible(true); regError.setManaged(true); return;
            }

            // Generate Patient ID before creating anything
            String patientId = IDGenerator.generatePatientId();

            if (!AuthenticationService.registerPatientAccount(username, password, patientId, email)) {
                regError.setText("Username already taken."); regError.setVisible(true); regError.setManaged(true); return;
            }

            // Create Patient record
            int age = Integer.parseInt(ageStr);
            Patient patient = new Patient("", name, phone, email, patientId, age, gender, "", blood != null ? blood : "");
            List<Patient> patients = FileManager.loadPatients();
            patients.add(patient);
            FileManager.savePatients(patients);

            PatientUserMapper.link(username, patientId);

            AlertUtil.showSuccess("Registration Successful",
                "Account created for " + name + " (ID: " + patientId + ")\nYou can now login.");
            showLoginForm.run();
            txtUsername.setText(username);
        });

        Scene scene = new Scene(root, 1000, 620);
        scene.getStylesheets().add(getClass().getResource("/com/cityhospital/style.css").toExternalForm());
        return scene;
    }
}
