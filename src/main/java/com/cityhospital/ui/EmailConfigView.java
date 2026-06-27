package com.cityhospital.ui;

import com.cityhospital.model.EmailConfig;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class EmailConfigView {

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("EMAIL / SMTP CONFIGURATION");
        title.getStyleClass().add("page-title");

        EmailConfig config = FileManager.loadEmailConfig();

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        TextField fHost = new TextField(config.getSmtpHost()); fHost.setPromptText("smtp.gmail.com"); fHost.getStyleClass().add("text-field");
        TextField fPort = new TextField(String.valueOf(config.getSmtpPort())); fPort.setPromptText("587"); fPort.getStyleClass().add("text-field");
        TextField fUser = new TextField(config.getUsername()); fUser.setPromptText("your@email.com"); fUser.getStyleClass().add("text-field");
        PasswordField fPass = new PasswordField(); fPass.setPromptText("App Password"); if (!config.getPassword().isEmpty()) fPass.setText(config.getPassword()); fPass.getStyleClass().add("password-field");
        TextField fFrom = new TextField(config.getFromAddress()); fFrom.setPromptText("from@email.com"); fFrom.getStyleClass().add("text-field");
        CheckBox chkTls = new CheckBox("Use TLS"); chkTls.setSelected(config.isUseTls());

        form.add(new Label("SMTP Host:"), 0, 0); form.add(fHost, 1, 0);
        form.add(new Label("Port:"), 0, 1); form.add(fPort, 1, 1);
        form.add(new Label("Username:"), 0, 2); form.add(fUser, 1, 2);
        form.add(new Label("Password:"), 0, 3); form.add(fPass, 1, 3);
        form.add(new Label("From Address:"), 0, 4); form.add(fFrom, 1, 4);
        form.add(new Label("Security:"), 0, 5); form.add(chkTls, 1, 5);

        Button btnSave = new Button("Save Configuration");
        btnSave.getStyleClass().addAll("btn", "btn-primary");

        Button btnTest = new Button("Send Test Email");
        btnTest.getStyleClass().addAll("btn", "btn-success");

        btnSave.setOnAction(e -> {
            try {
                EmailConfig cfg = new EmailConfig(
                    fHost.getText().trim(),
                    Integer.parseInt(fPort.getText().trim()),
                    fUser.getText().trim(),
                    fPass.getText().trim(),
                    fFrom.getText().trim(),
                    chkTls.isSelected()
                );
                FileManager.saveEmailConfig(cfg);
                AlertUtil.showSuccess("Saved", "Email configuration saved.");
            } catch (NumberFormatException ex) {
                AlertUtil.showError("Error", "Port must be a number.");
            }
        });

        btnTest.setOnAction(e -> {
            String testTo = fFrom.getText().trim();
            if (testTo.isEmpty()) { AlertUtil.showError("Error", "Set From Address first."); return; }
            boolean ok = com.cityhospital.service.EmailSender.sendEmail(testTo, "Test from City Hospital",
                    "This is a test email. Your SMTP configuration is working!");
            if (ok) AlertUtil.showSuccess("Success", "Test email sent to " + testTo);
            else AlertUtil.showError("Failed", "Could not send test email. Check your settings.");
        });

        VBox card = new VBox(15, form, btnSave, btnTest);
        card.getStyleClass().add("card");

        view.getChildren().addAll(title, card);
        return view;
    }
}
