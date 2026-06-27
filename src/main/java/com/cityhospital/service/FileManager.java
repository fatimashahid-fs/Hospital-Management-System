package com.cityhospital.service;

import com.cityhospital.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE HANDLING:
 * FileManager is the central class responsible for all data persistence.
 * 
 * It uses Java Serialization to save and load objects to/from disk files.
 * 
 * Key classes used:
 * - ObjectOutputStream / ObjectInputStream (for serialization/deserialization)
 * - FileOutputStream / FileInputStream (for file I/O)
 * - File (for file system operations)
 * 
 * Data files are stored in the "data/" directory:
 * - data/users.dat
 * - data/patients.dat
 * - data/doctors.dat
 * - data/appointments.dat
 * 
 * Files are automatically created if they do not exist.
 * All I/O exceptions are handled with meaningful error messages.
 */
public class FileManager {

    // File paths for data storage
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.dat";
    private static final String PATIENTS_FILE = DATA_DIR + "/patients.dat";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctors.dat";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.dat";
    private static final String BILLS_FILE = DATA_DIR + "/bills.dat";
    private static final String MEDICINES_FILE = DATA_DIR + "/medicines.dat";
    private static final String NOTIFICATIONS_FILE = DATA_DIR + "/notifications.dat";
    private static final String EMAIL_CONFIG_FILE = DATA_DIR + "/email_config.dat";
    private static final String EMERGENCY_CASES_FILE = DATA_DIR + "/emergency_cases.dat";
    private static final String PRESCRIPTIONS_FILE = DATA_DIR + "/prescriptions.dat";
    private static final String FEEDBACK_FILE = DATA_DIR + "/feedback.dat";

    /**
     * Ensures the data directory exists.
     * Creates it if it doesn't exist.
     * 
     * @throws IOException if the directory cannot be created
     */
    private static void ensureDataDir() throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Could not create data directory: " + DATA_DIR);
            }
        }
    }

    /**
     * Generic method to save a list of Serializable objects to a file.
     * 
     * @param <T>      The type of objects in the list
     * @param list     The list to save
     * @param filePath The file path to save to
     */
    private static <T extends Serializable> void saveList(List<T> list, String filePath) {
        try {
            ensureDataDir();
            File file = new File(filePath);
            // If the file doesn't exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // Write the list using ObjectOutputStream
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(list);
            }
        } catch (IOException e) {
            System.err.println("Error saving to " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Generic method to load a list of Serializable objects from a file.
     * If the file doesn't exist or is empty, returns an empty list.
     * 
     * @param <T>      The type of objects in the list
     * @param filePath The file path to load from
     * @return The loaded list, or an empty list if load fails
     */
    @SuppressWarnings("unchecked")
    private static <T extends Serializable> List<T> loadList(String filePath) {
        List<T> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return list;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                list = (List<T>) obj;
            }
        } catch (FileNotFoundException e) {
            // File not found yet - return empty list
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from " + filePath + ": " + e.getMessage());
        }
        return list;
    }

    // ============================================================
    // USER OPERATIONS
    // ============================================================

    /** Saves the list of users to users.dat */
    public static void saveUsers(List<User> users) {
        saveList(users, USERS_FILE);
    }

    /** Loads the list of users from users.dat */
    public static List<User> loadUsers() {
        return loadList(USERS_FILE);
    }

    // ============================================================
    // PATIENT OPERATIONS
    // ============================================================

    /** Saves the list of patients to patients.dat */
    public static void savePatients(List<Patient> patients) {
        saveList(patients, PATIENTS_FILE);
    }

    /** Loads the list of patients from patients.dat */
    public static List<Patient> loadPatients() {
        return loadList(PATIENTS_FILE);
    }

    // ============================================================
    // DOCTOR OPERATIONS
    // ============================================================

    /** Saves the list of doctors to doctors.dat */
    public static void saveDoctors(List<Doctor> doctors) {
        saveList(doctors, DOCTORS_FILE);
    }

    /** Loads the list of doctors from doctors.dat */
    public static List<Doctor> loadDoctors() {
        return loadList(DOCTORS_FILE);
    }

    // ============================================================
    // APPOINTMENT OPERATIONS
    // ============================================================

    /** Saves the list of appointments to appointments.dat */
    public static void saveAppointments(List<Appointment> appointments) {
        saveList(appointments, APPOINTMENTS_FILE);
    }

    /** Loads the list of appointments from appointments.dat */
    public static List<Appointment> loadAppointments() {
        return loadList(APPOINTMENTS_FILE);
    }

    // ============================================================
    // BILL OPERATIONS
    // ============================================================

    /** Saves the list of bills to bills.dat */
    public static void saveBills(List<Bill> bills) {
        saveList(bills, BILLS_FILE);
    }

    /** Loads the list of bills from bills.dat */
    public static List<Bill> loadBills() {
        return loadList(BILLS_FILE);
    }

    // ============================================================
    // MEDICINE OPERATIONS
    // ============================================================

    /** Saves the list of medicines to medicines.dat */
    public static void saveMedicines(List<Medicine> medicines) {
        saveList(medicines, MEDICINES_FILE);
    }

    /** Loads the list of medicines from medicines.dat */
    public static List<Medicine> loadMedicines() {
        return loadList(MEDICINES_FILE);
    }

    // ============================================================
    // NOTIFICATION HISTORY OPERATIONS
    // ============================================================

    /** Saves notification history to notifications.dat */
    public static void saveNotifications(List<NotificationRecord> records) {
        saveList(records, NOTIFICATIONS_FILE);
    }

    /** Loads notification history from notifications.dat */
    public static List<NotificationRecord> loadNotifications() {
        return loadList(NOTIFICATIONS_FILE);
    }

    // ============================================================
    // EMAIL CONFIG OPERATIONS
    // ============================================================

    /** Saves email SMTP config to email_config.dat */
    public static void saveEmailConfig(EmailConfig config) {
        try {
            ensureDataDir();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMAIL_CONFIG_FILE))) {
                oos.writeObject(config);
            }
        } catch (IOException e) {
            System.err.println("Error saving email config: " + e.getMessage());
        }
    }

    /** Loads email SMTP config from email_config.dat */
    public static EmailConfig loadEmailConfig() {
        File file = new File(EMAIL_CONFIG_FILE);
        if (!file.exists() || file.length() == 0) {
            return new EmailConfig();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (EmailConfig) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading email config: " + e.getMessage());
            return new EmailConfig();
        }
    }

    // ============================================================
    // EMERGENCY CASE OPERATIONS
    // ============================================================

    public static void saveEmergencyCases(List<EmergencyCase> cases) {
        saveList(cases, EMERGENCY_CASES_FILE);
    }

    public static List<EmergencyCase> loadEmergencyCases() {
        return loadList(EMERGENCY_CASES_FILE);
    }

    // ============================================================
    // PRESCRIPTION OPERATIONS
    // ============================================================

    public static void savePrescriptions(List<Prescription> prescriptions) {
        saveList(prescriptions, PRESCRIPTIONS_FILE);
    }

    public static List<Prescription> loadPrescriptions() {
        return loadList(PRESCRIPTIONS_FILE);
    }

    // ============================================================
    // FEEDBACK OPERATIONS
    // ============================================================

    public static void saveFeedback(List<Feedback> feedback) {
        saveList(feedback, FEEDBACK_FILE);
    }

    public static List<Feedback> loadFeedback() {
        return loadList(FEEDBACK_FILE);
    }
}
