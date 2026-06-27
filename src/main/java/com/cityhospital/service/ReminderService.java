package com.cityhospital.service;

import com.cityhospital.model.Appointment;
import com.cityhospital.model.Doctor;
import com.cityhospital.model.Patient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReminderService {

    public static void sendStartupReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        var appointments = FileManager.loadAppointments();

        for (Appointment a : appointments) {
            if (!a.getAppointmentDate().equals(tomorrow)) continue;
            if (!Appointment.STATUS_SCHEDULED.equals(a.getStatus())) continue;

            Patient patient = FileManager.loadPatients().stream()
                    .filter(p -> p.getPatientId().equals(a.getPatientId()))
                    .findFirst().orElse(null);
            if (patient == null) continue;

            Doctor doctor = FileManager.loadDoctors().stream()
                    .filter(d -> d.getDoctorId().equals(a.getDoctorId()))
                    .findFirst().orElse(null);

            String doctorName = doctor != null ? doctor.getName() : "Unknown";
            String dateStr = a.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String timeStr = a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            NotificationService.sendAppointmentReminderSilent(
                    patient.getName(),
                    patient.getEmail(),
                    patient.getPhone(),
                    doctorName,
                    dateStr,
                    timeStr,
                    a.getAppointmentId()
            );
        }
    }

    public static void sendReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        var appointments = FileManager.loadAppointments();

        for (Appointment a : appointments) {
            if (!a.getAppointmentDate().equals(tomorrow)) continue;
            if (!Appointment.STATUS_SCHEDULED.equals(a.getStatus())) continue;

            Patient patient = FileManager.loadPatients().stream()
                    .filter(p -> p.getPatientId().equals(a.getPatientId()))
                    .findFirst().orElse(null);
            if (patient == null) continue;

            Doctor doctor = FileManager.loadDoctors().stream()
                    .filter(d -> d.getDoctorId().equals(a.getDoctorId()))
                    .findFirst().orElse(null);

            String doctorName = doctor != null ? doctor.getName() : "Unknown";
            String dateStr = a.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String timeStr = a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            NotificationService.sendAppointmentReminder(
                    patient.getName(),
                    patient.getEmail(),
                    patient.getPhone(),
                    doctorName,
                    dateStr,
                    timeStr,
                    a.getAppointmentId()
            );
        }
    }
}
