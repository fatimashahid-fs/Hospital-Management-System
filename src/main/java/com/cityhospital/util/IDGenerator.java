package com.cityhospital.util;

import com.cityhospital.model.*;
import com.cityhospital.service.FileManager;
import java.util.List;

/**
 * IDGenerator is a utility class that automatically generates
 * sequential IDs for Patients, Doctors, Appointments, and Users.
 * 
 * It reads the existing data from files to determine the next available ID.
 */
public class IDGenerator {

    /**
     * Generates the next Patient ID.
     * Reads existing patients and increments the highest number found.
     * Format: PAT001, PAT002, ...
     * 
     * @return Next available patient ID as a String
     */
    public static String generatePatientId() {
        List<Patient> patients = FileManager.loadPatients();
        int maxId = 0;
        for (Patient p : patients) {
            String id = p.getPatientId();
            if (id != null && id.startsWith("PAT")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip malformed IDs
                }
            }
        }
        return String.format("PAT%03d", maxId + 1);
    }

    /**
     * Generates the next Doctor ID.
     * Format: DOC001, DOC002, ...
     * 
     * @return Next available doctor ID
     */
    public static String generateDoctorId() {
        List<Doctor> doctors = FileManager.loadDoctors();
        int maxId = 0;
        for (Doctor d : doctors) {
            String id = d.getDoctorId();
            if (id != null && id.startsWith("DOC")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip malformed IDs
                }
            }
        }
        return String.format("DOC%03d", maxId + 1);
    }

    /**
     * Generates the next Appointment ID.
     * Format: APT001, APT002, ...
     * 
     * @return Next available appointment ID
     */
    public static String generateAppointmentId() {
        List<Appointment> appointments = FileManager.loadAppointments();
        int maxId = 0;
        for (Appointment a : appointments) {
            String id = a.getAppointmentId();
            if (id != null && id.startsWith("APT")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip malformed IDs
                }
            }
        }
        return String.format("APT%03d", maxId + 1);
    }

    /**
     * Generates the next User ID.
     * Format: USR001, USR002, ...
     * 
     * @return Next available user ID
     */
    public static String generateUserId() {
        List<User> users = FileManager.loadUsers();
        int maxId = 0;
        for (User u : users) {
            String id = u.getUserId();
            if (id != null && id.startsWith("USR")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip malformed IDs
                }
            }
        }
        return String.format("USR%03d", maxId + 1);
    }

    /**
     * Generates the next Bill ID.
     * Format: BIL001, BIL002, ...
     * 
     * @return Next available bill ID
     */
    public static String generateBillId() {
        List<Bill> bills = FileManager.loadBills();
        int maxId = 0;
        for (Bill b : bills) {
            String id = b.getBillId();
            if (id != null && id.startsWith("BIL")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("BIL%03d", maxId + 1);
    }

    /**
     * Generates the next Medicine ID.
     * Format: MED001, MED002, ...
     * 
     * @return Next available medicine ID
     */
    public static String generateMedicineId() {
        List<Medicine> meds = FileManager.loadMedicines();
        int maxId = 0;
        for (Medicine m : meds) {
            String id = m.getMedicineId();
            if (id != null && id.startsWith("MED")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("MED%03d", maxId + 1);
    }

    /**
     * Generates the next Notification ID.
     * Format: NOT001, NOT002, ...
     */
    public static String generateNotificationId() {
        List<NotificationRecord> records = FileManager.loadNotifications();
        int maxId = 0;
        for (NotificationRecord r : records) {
            String id = r.getId();
            if (id != null && id.startsWith("NOT")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("NOT%03d", maxId + 1);
    }

    /**
     * Generates the next Emergency Case ID.
     * Format: EMC001, EMC002, ...
     */
    public static String generateEmergencyCaseId() {
        List<EmergencyCase> cases = FileManager.loadEmergencyCases();
        int maxId = 0;
        for (EmergencyCase c : cases) {
            String id = c.getCaseId();
            if (id != null && id.startsWith("EMC")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("EMC%03d", maxId + 1);
    }

    /**
     * Generates the next Prescription ID.
     * Format: PRX001, PRX002, ...
     */
    public static String generatePrescriptionId() {
        List<Prescription> list = FileManager.loadPrescriptions();
        int maxId = 0;
        for (Prescription p : list) {
            String id = p.getPrescriptionId();
            if (id != null && id.startsWith("PRX")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("PRX%03d", maxId + 1);
    }

    /**
     * Generates the next Feedback ID.
     * Format: FDB001, FDB002, ...
     */
    public static String generateFeedbackId() {
        List<Feedback> list = FileManager.loadFeedback();
        int maxId = 0;
        for (Feedback f : list) {
            String id = f.getFeedbackId();
            if (id != null && id.startsWith("FDB")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("FDB%03d", maxId + 1);
    }
}
