package com.cityhospital.model;

import java.io.Serializable;

/**
 * CLASS AND OBJECT (OOP Concept):
 * User is a concrete class that represents a system user with login credentials.
 * Objects of this class are created to represent each person who can log into the system.
 * 
 * ENCAPSULATION (OOP Concept):
 * All fields are private with public getters and setters.
 * 
 * Implements Serializable for file handling persistence via serialization.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_RECEPTIONIST = "RECEPTIONIST";
    public static final String ROLE_PATIENT = "PATIENT";

    private String userId;
    private String username;
    private String password;
    private String role;
    private String patientId;
    private String patientEmail;

    public User(String userId, String username, String password, String role) {
        this(userId, username, password, role, null, null);
    }

    public User(String userId, String username, String password, String role, String patientId) {
        this(userId, username, password, role, patientId, null);
    }

    public User(String userId, String username, String password, String role, String patientId, String patientEmail) {
        this.userId = userId; this.username = username; this.password = password;
        this.role = role; this.patientId = patientId; this.patientEmail = patientEmail;
    }

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
}
