package com.cityhospital.service;

import com.cityhospital.model.User;
import com.cityhospital.util.IDGenerator;
import java.util.List;

/**
 * AUTHENTICATION SERVICE:
 * Handles user login and default account creation.
 * 
 * On first application run (when no users.dat exists), this service
 * automatically creates the four default accounts:
 * - Admin (admin/admin123)
 * - Doctor (doctor/doctor123)
 * - Receptionist (reception/reception123)
 * - Patient (patient/patient123)
 */
public class AuthenticationService {

    /**
     * Initializes default accounts if the users file is empty.
     * Called once at application startup.
     * 
     * ROLE-BASED ACCESS CONTROL: Each user is assigned a role
     * that determines which dashboards and features they can access.
     */
    public static void initializeDefaultUsers() {
        List<User> users = FileManager.loadUsers();
        if (users.isEmpty()) {
            users.add(new User("USR001", "admin", "admin123", User.ROLE_ADMIN));
            users.add(new User("USR002", "doctor", "doctor123", User.ROLE_DOCTOR));
            users.add(new User("USR003", "reception", "reception123", User.ROLE_RECEPTIONIST));
            users.add(new User("USR004", "patient", "patient123", User.ROLE_PATIENT, null, "patient@email.com"));
            FileManager.saveUsers(users);
            System.out.println("Default users created successfully.");
        }
    }

    /**
     * Authenticates a user by checking the username and password
     * against the stored users list.
     * 
     * @param username The entered username
     * @param password The entered password
     * @return The User object if authentication succeeds, null otherwise
     */
    public static User authenticate(String username, String password) {
        List<User> users = FileManager.loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // Authentication failed
    }

    /**
     * Registers a new patient user account linked to their Patient record.
     *
     * @param username     Desired username
     * @param password     Desired password
     * @param patientId    The Patient ID (PATxxx)
     * @param patientEmail The patient's email for reliable matching
     * @return true if account was created successfully
     */
    public static boolean registerPatientAccount(String username, String password, String patientId, String patientEmail) {
        List<User> users = FileManager.loadUsers();
        boolean exists = users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
        if (exists) {
            return false;
        }
        String userId = IDGenerator.generateUserId();
        users.add(new User(userId, username, password, User.ROLE_PATIENT, patientId, patientEmail));
        FileManager.saveUsers(users);
        return true;
    }
}
