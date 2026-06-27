package com.cityhospital.service;

import com.cityhospital.model.NotificationRecord;
import com.cityhospital.util.IDGenerator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDateTime;

public class NotificationService {

    private static void saveToHistory(String type, String recipient, String subject, String message, String status) {
        String id = IDGenerator.generateNotificationId();
        NotificationRecord rec = new NotificationRecord(id, type, recipient, subject, message, status, LocalDateTime.now());
        var records = FileManager.loadNotifications();
        records.add(rec);
        FileManager.saveNotifications(records);
    }

    public static void sendNotification(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        saveToHistory(NotificationRecord.TYPE_ALERT, "", "System Notification", message, NotificationRecord.STATUS_SENT);
    }

    public static void sendNotification(String message, String email) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        boolean configured = isEmailConfigured();
        alert.setContentText(message + (configured ? "\n\nEmail sent to: " + email : "\n\nEmail not configured."));
        alert.showAndWait();

        boolean sent = configured && EmailSender.sendEmail(email, "THE CITY HOSPITAL - Notification", message);
        String status = !configured ? "Not Configured" : sent ? NotificationRecord.STATUS_SENT : NotificationRecord.STATUS_FAILED;
        saveToHistory(NotificationRecord.TYPE_EMAIL, email, "Appointment Notification", message, status);
    }

    public static void sendNotification(String message, String email, String phone) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        boolean configured = isEmailConfigured();
        String display = message + (configured ? "\n\nEmail sent to: " + email : "\n\nEmail not configured.") + "\nSMS sent to: " + phone;
        alert.setContentText(display);
        alert.showAndWait();

        boolean emailSent = configured && EmailSender.sendEmail(email, "THE CITY HOSPITAL - Notification", message);
        String emailStatus = !configured ? "Not Configured" : emailSent ? NotificationRecord.STATUS_SENT : NotificationRecord.STATUS_FAILED;
        saveToHistory(NotificationRecord.TYPE_EMAIL, email, "Appointment Notification", message, emailStatus);

        System.out.println("[SMS] To: " + phone + " | " + message);
        saveToHistory(NotificationRecord.TYPE_SMS, phone, "SMS Notification", message, NotificationRecord.STATUS_SENT);
    }

    private static boolean isEmailConfigured() {
        var config = FileManager.loadEmailConfig();
        return config.getSmtpHost() != null && !config.getSmtpHost().isEmpty()
            && config.getUsername() != null && !config.getUsername().isEmpty();
    }

    public static void sendAppointmentReminderSilent(String patientName, String patientEmail, String patientPhone,
                                                      String doctorName, String date, String time, String appointmentId) {
        String subject = "Appointment Reminder";
        String body = "Dear " + patientName + ",\n\n"
                + "This is a reminder for your upcoming appointment.\n\n"
                + "Doctor: Dr. " + doctorName + "\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n"
                + "Appointment ID: " + appointmentId + "\n\n"
                + "Please arrive 15 minutes early.\n"
                + "THE CITY HOSPITAL";

        boolean emailSent = false;
        boolean configured = isEmailConfigured();
        if (patientEmail != null && !patientEmail.isEmpty() && configured) {
            emailSent = EmailSender.sendEmail(patientEmail, subject, body);
        }
        String emailStatus = !configured ? "Not Configured" : emailSent ? NotificationRecord.STATUS_SENT : NotificationRecord.STATUS_FAILED;
        saveToHistory(NotificationRecord.TYPE_EMAIL, patientEmail, subject, body, emailStatus);

        if (patientPhone != null && !patientPhone.isEmpty()) {
            System.out.println("[SMS Reminder] To: " + patientPhone + " | " + body);
            saveToHistory(NotificationRecord.TYPE_SMS, patientPhone, "SMS Reminder", body, NotificationRecord.STATUS_SENT);
        }
    }

    public static void sendAppointmentReminder(String patientName, String patientEmail, String patientPhone,
                                                String doctorName, String date, String time, String appointmentId) {
        String subject = "Appointment Reminder";
        String body = "Dear " + patientName + ",\n\n"
                + "This is a reminder for your upcoming appointment.\n\n"
                + "Doctor: Dr. " + doctorName + "\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n"
                + "Appointment ID: " + appointmentId + "\n\n"
                + "Please arrive 15 minutes early.\n"
                + "THE CITY HOSPITAL";

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Appointment Reminder");
        alert.setHeaderText(null);
        boolean configured = isEmailConfigured();
        String emailLine = configured ? "\n\nEmail sent to: " + patientEmail : "\n\nEmail not configured.";
        alert.setContentText(body + emailLine + "\nSMS sent to: " + patientPhone);
        alert.showAndWait();

        boolean emailSent = false;
        if (patientEmail != null && !patientEmail.isEmpty() && configured) {
            emailSent = EmailSender.sendEmail(patientEmail, subject, body);
        }
        String emailStatus = !configured ? "Not Configured" : emailSent ? NotificationRecord.STATUS_SENT : NotificationRecord.STATUS_FAILED;
        saveToHistory(NotificationRecord.TYPE_EMAIL, patientEmail, subject, body, emailStatus);

        if (patientPhone != null && !patientPhone.isEmpty()) {
            System.out.println("[SMS Reminder] To: " + patientPhone + " | " + body);
            saveToHistory(NotificationRecord.TYPE_SMS, patientPhone, "SMS Reminder", body, NotificationRecord.STATUS_SENT);
        }
    }
}
