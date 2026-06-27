package com.cityhospital.controller;

import com.cityhospital.model.Appointment;
import com.cityhospital.model.Doctor;
import com.cityhospital.model.EmergencyCase;
import com.cityhospital.model.Patient;
import com.cityhospital.service.FileManager;
import com.cityhospital.service.NotificationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.DateTimeUtil;
import com.cityhospital.util.IDGenerator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AppointmentController manages all appointment operations.
 * 
 * CONFLICT DETECTION:
 * Before booking, checks: (1) Doctor availability - no two appointments for same doctor at same date/time
 * (2) Patient availability - no two appointments for same patient at same date/time
 */
public class AppointmentController {

    public boolean bookAppointment(String patientId, String doctorId, String dateStr, String timeStr) {
        return bookAppointment(patientId, doctorId, dateStr, timeStr, Appointment.URGENCY_NORMAL);
    }

    public boolean bookAppointment(String patientId, String doctorId, String dateStr, String timeStr, String urgency) {
        LocalDate date = DateTimeUtil.parseDate(dateStr);
        LocalTime time = DateTimeUtil.parseTime(timeStr);
        if (!validateInput(patientId, doctorId, date, time)) return false;

        List<Appointment> appointments = FileManager.loadAppointments();
        if (isDoctorConflict(appointments, doctorId, date, time, null)) {
            AlertUtil.showError("Conflict Detected", "This doctor already has an appointment at this date and time.");
            return false;
        }
        if (isPatientConflict(appointments, patientId, date, time, null)) {
            AlertUtil.showError("Conflict Detected", "This patient already has an appointment at this date and time.");
            return false;
        }

        String apptId = IDGenerator.generateAppointmentId();
        appointments.add(new Appointment(apptId, patientId, doctorId, date, time, Appointment.STATUS_SCHEDULED, urgency));
        FileManager.saveAppointments(appointments);

        autoCreateEmergencyCase(patientId, doctorId, urgency, apptId);
        notifyPatient(patientId, "Appointment booked successfully.\nAppointment ID: " + apptId);
        return true;
    }

    public boolean bookAppointmentPending(String patientId, String doctorId, String dateStr, String timeStr) {
        return bookAppointmentPending(patientId, doctorId, dateStr, timeStr, Appointment.URGENCY_NORMAL);
    }

    public boolean bookAppointmentPending(String patientId, String doctorId, String dateStr, String timeStr, String urgency) {
        LocalDate date = DateTimeUtil.parseDate(dateStr);
        LocalTime time = DateTimeUtil.parseTime(timeStr);
        if (!validateInput(patientId, doctorId, date, time)) return false;

        List<Appointment> appointments = FileManager.loadAppointments();

        boolean conflict = appointments.stream().anyMatch(a ->
                a.getDoctorId().equals(doctorId)
                && a.getAppointmentDate().equals(date)
                && a.getAppointmentTime().equals(time)
                && (a.getStatus().equals(Appointment.STATUS_SCHEDULED) || a.getStatus().equals(Appointment.STATUS_PENDING)));

        if (conflict) {
            List<String> freeSlots = getAvailableSlots(doctorId, DateTimeUtil.formatDate(date), appointments);
            if (!freeSlots.isEmpty()) {
                String suggestion = String.join(", ", freeSlots.subList(0, Math.min(3, freeSlots.size())));
                AlertUtil.showWarning("Slot Unavailable", "This slot is booked.\n\nAvailable slots: " + suggestion);
            } else {
                AlertUtil.showWarning("Slot Unavailable", "No slots available on this date.\nTry a different date or doctor.");
            }
            return false;
        }

        String apptId = IDGenerator.generateAppointmentId();
        appointments.add(new Appointment(apptId, patientId, doctorId, date, time, Appointment.STATUS_PENDING, urgency));
        FileManager.saveAppointments(appointments);

        autoCreateEmergencyCase(patientId, doctorId, urgency, apptId);
        notifyPatient(patientId, "Appointment request submitted (Pending).\nAppointment ID: " + apptId);
        AlertUtil.showSuccess("Success", "Appointment request submitted!\nIt will be reviewed by the receptionist.");
        return true;
    }

    public List<String> getAvailableSlots(String doctorId, String dateStr) {
        return getAvailableSlots(doctorId, dateStr, FileManager.loadAppointments());
    }

    private List<String> getAvailableSlots(String doctorId, String dateStr, List<Appointment> appointments) {
        LocalDate date = DateTimeUtil.parseDate(dateStr);
        if (date == null) return new ArrayList<>();

        List<String> takenSlots = appointments.stream()
                .filter(a -> a.getDoctorId().equals(doctorId)
                        && a.getAppointmentDate().equals(date)
                        && (a.getStatus().equals(Appointment.STATUS_SCHEDULED) || a.getStatus().equals(Appointment.STATUS_PENDING)))
                .map(a -> DateTimeUtil.formatTime(a.getAppointmentTime()))
                .toList();

        return DateTimeUtil.getTimeSlots().stream()
                .filter(s -> !takenSlots.contains(s))
                .collect(Collectors.toList());
    }

    public boolean approveAppointment(String appointmentId) {
        List<Appointment> appointments = FileManager.loadAppointments();
        Appointment target = findAppointment(appointments, appointmentId);
        if (target == null) { AlertUtil.showError("Error", "Appointment not found."); return false; }
        if (!Appointment.STATUS_PENDING.equals(target.getStatus())) {
            AlertUtil.showError("Error", "Only pending appointments can be approved."); return false;
        }
        if (isDoctorConflict(appointments, target.getDoctorId(), target.getAppointmentDate(), target.getAppointmentTime(), appointmentId)) {
            AlertUtil.showError("Conflict", "This doctor already has an appointment at this time.\nReject the request.");
            return false;
        }
        if (isPatientConflict(appointments, target.getPatientId(), target.getAppointmentDate(), target.getAppointmentTime(), appointmentId)) {
            AlertUtil.showError("Conflict", "This patient already has an appointment at this time.");
            return false;
        }

        target.setStatus(Appointment.STATUS_SCHEDULED);
        FileManager.saveAppointments(appointments);

        if (Appointment.URGENCY_EMERGENCY.equals(target.getUrgency()) || Appointment.URGENCY_URGENT.equals(target.getUrgency())) {
            EmergencyCaseController ecc = new EmergencyCaseController();
            ecc.getOpenCases().stream()
                    .filter(c -> target.getAppointmentId().equals(c.getAppointmentId()))
                    .findFirst()
                    .ifPresent(c -> ecc.updateStatus(c.getCaseId(), EmergencyCase.STATUS_IN_PROGRESS));
        }
        NotificationService.sendNotification("Appointment " + appointmentId + " approved.");
        notifyPatient(target.getPatientId(), "Your appointment " + appointmentId + " has been approved.\nDoctor: " + target.getDoctorId() + "\nDate: " + target.getAppointmentDate() + "\nTime: " + target.getAppointmentTime());
        // Notify doctor via email
        Doctor doc = FileManager.loadDoctors().stream()
                .filter(d -> d.getDoctorId().equals(target.getDoctorId()))
                .findFirst().orElse(null);
        if (doc != null && doc.getEmail() != null && !doc.getEmail().isEmpty()) {
            NotificationService.sendNotification("New appointment scheduled.\nPatient: " + target.getPatientId() + "\nDate: " + target.getAppointmentDate() + "\nTime: " + target.getAppointmentTime(), doc.getEmail());
        }
        return true;
    }

    public boolean rejectAppointment(String appointmentId) {
        return setAppointmentStatus(appointmentId, Appointment.STATUS_CANCELLED, "Appointment " + appointmentId + " was rejected.");
    }

    public boolean cancelAppointment(String appointmentId) {
        return setAppointmentStatus(appointmentId, Appointment.STATUS_CANCELLED, "Appointment cancelled successfully.");
    }

    public boolean completeAppointment(String appointmentId) {
        return setAppointmentStatus(appointmentId, Appointment.STATUS_COMPLETED, "Appointment marked as completed.");
    }

    private boolean setAppointmentStatus(String appointmentId, String newStatus, String notificationMsg) {
        List<Appointment> appointments = FileManager.loadAppointments();
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus(newStatus);
                FileManager.saveAppointments(appointments);
                NotificationService.sendNotification(notificationMsg);
                return true;
            }
        }
        AlertUtil.showError("Error", "Appointment not found.");
        return false;
    }

    public boolean updateAppointment(Appointment appointment) {
        if (appointment == null) { AlertUtil.showError("Error", "No appointment selected."); return false; }
        List<Appointment> appointments = FileManager.loadAppointments();

        if (isDoctorConflict(appointments, appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment.getAppointmentId())) {
            AlertUtil.showError("Conflict Detected", "Doctor already has an appointment at this time."); return false;
        }
        if (isPatientConflict(appointments, appointment.getPatientId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment.getAppointmentId())) {
            AlertUtil.showError("Conflict Detected", "Patient already has an appointment at this time."); return false;
        }

        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId().equals(appointment.getAppointmentId())) {
                appointments.set(i, appointment);
                FileManager.saveAppointments(appointments);
                NotificationService.sendNotification("Appointment updated successfully.");
                return true;
            }
        }
        AlertUtil.showError("Error", "Appointment not found.");
        return false;
    }

    public boolean rescheduleAppointment(String appointmentId, String newDateStr, String newTimeStr) {
        LocalDate newDate = DateTimeUtil.parseDate(newDateStr);
        LocalTime newTime = DateTimeUtil.parseTime(newTimeStr);
        if (newDate == null || newTime == null) { AlertUtil.showError("Error", "Invalid date or time."); return false; }

        List<Appointment> appointments = FileManager.loadAppointments();
        Appointment target = findAppointment(appointments, appointmentId);
        if (target == null) { AlertUtil.showError("Error", "Appointment not found."); return false; }

        if (isDoctorConflict(appointments, target.getDoctorId(), newDate, newTime, appointmentId)) {
            AlertUtil.showError("Conflict Detected", "Doctor already has an appointment at this new time.");
            return false;
        }

        target.setAppointmentDate(newDate);
        target.setAppointmentTime(newTime);
        FileManager.saveAppointments(appointments);
        notifyPatient(target.getPatientId(), "Appointment " + appointmentId + " rescheduled to " + newDateStr + " at " + newTimeStr);
        AlertUtil.showSuccess("Success", "Appointment rescheduled successfully.");
        return true;
    }

    public List<Appointment> searchAppointments(String query) {
        if (query == null || query.trim().isEmpty()) return FileManager.loadAppointments();
        String q = query.toLowerCase();
        return FileManager.loadAppointments().stream()
                .filter(a -> a.getAppointmentId().toLowerCase().contains(q)
                        || a.getPatientId().toLowerCase().contains(q)
                        || a.getDoctorId().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAllAppointments() { return FileManager.loadAppointments(); }

    public List<Appointment> getAllAppointmentsSorted() {
        return sortByUrgency(FileManager.loadAppointments());
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return FileManager.loadAppointments().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctorSorted(String doctorId) {
        return sortByUrgency(getAppointmentsByDoctor(doctorId));
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return FileManager.loadAppointments().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByPatientSorted(String patientId) {
        return sortByUrgency(getAppointmentsByPatient(patientId));
    }

    public Appointment getAppointmentById(String appointmentId) {
        return findAppointment(FileManager.loadAppointments(), appointmentId);
    }

    // ===== Helper methods =====

    private boolean validateInput(String patientId, String doctorId, LocalDate date, LocalTime time) {
        if (patientId == null || patientId.trim().isEmpty()) { AlertUtil.showError("Error", "Please select a patient."); return false; }
        if (doctorId == null || doctorId.trim().isEmpty()) { AlertUtil.showError("Error", "Please select a doctor."); return false; }
        if (date == null) { AlertUtil.showError("Error", "Invalid date format."); return false; }
        if (date.isBefore(LocalDate.now())) { AlertUtil.showError("Error", "Date cannot be in the past."); return false; }
        if (time == null) { AlertUtil.showError("Error", "Invalid time format."); return false; }
        return true;
    }

    private boolean isDoctorConflict(List<Appointment> appointments, String doctorId, LocalDate date, LocalTime time, String excludeId) {
        return appointments.stream().anyMatch(a ->
                !a.getAppointmentId().equals(excludeId != null ? excludeId : "")
                && a.getDoctorId().equals(doctorId)
                && a.getAppointmentDate().equals(date)
                && a.getAppointmentTime().equals(time)
                && !a.getStatus().equals(Appointment.STATUS_CANCELLED));
    }

    private boolean isPatientConflict(List<Appointment> appointments, String patientId, LocalDate date, LocalTime time, String excludeId) {
        return appointments.stream().anyMatch(a ->
                !a.getAppointmentId().equals(excludeId != null ? excludeId : "")
                && a.getPatientId().equals(patientId)
                && a.getAppointmentDate().equals(date)
                && a.getAppointmentTime().equals(time)
                && !a.getStatus().equals(Appointment.STATUS_CANCELLED));
    }

    private Appointment findAppointment(List<Appointment> appointments, String appointmentId) {
        return appointments.stream()
                .filter(a -> a.getAppointmentId().equals(appointmentId))
                .findFirst().orElse(null);
    }

    private List<Appointment> sortByUrgency(List<Appointment> list) {
        return list.stream()
                .sorted((a, b) -> Integer.compare(
                        Appointment.urgencyPriority(b.getUrgency()),
                        Appointment.urgencyPriority(a.getUrgency())))
                .collect(Collectors.toList());
    }

    private void autoCreateEmergencyCase(String patientId, String doctorId, String urgency, String appointmentId) {
        if (Appointment.URGENCY_EMERGENCY.equals(urgency) || Appointment.URGENCY_URGENT.equals(urgency)) {
            EmergencyCaseController ecc = new EmergencyCaseController();
            String desc = Appointment.URGENCY_EMERGENCY.equals(urgency)
                    ? "Emergency case auto-created for appointment " + appointmentId
                    : "Urgent case auto-created for appointment " + appointmentId;
            ecc.createCase(patientId, doctorId, urgency, desc, appointmentId);
        }
    }

    private void notifyPatient(String patientId, String message) {
        Patient patient = FileManager.loadPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst().orElse(null);
        if (patient != null && patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            NotificationService.sendNotification(message, patient.getEmail());
        } else {
            NotificationService.sendNotification(message);
        }
    }
}
