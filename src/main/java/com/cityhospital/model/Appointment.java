package com.cityhospital.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * CLASS AND OBJECT (OOP Concept):
 * Appointment represents a scheduled appointment between a patient and a doctor.
 * Objects of this class encapsulate all appointment-related data.
 * 
 * ENCAPSULATION (OOP Concept):
 * All fields are private. Access is through public getters and setters.
 * 
 * Implements Serializable for file-based persistence.
 */
public class Appointment implements Serializable {
    private static final long serialVersionUID = 2L;

    // --- Status Constants ---
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    // --- Urgency Constants ---
    public static final String URGENCY_EMERGENCY = "Emergency";
    public static final String URGENCY_URGENT = "Urgent";
    public static final String URGENCY_NORMAL = "Normal";

    /** Priority order for sorting (higher = more urgent). */
    public static int urgencyPriority(String urgency) {
        return switch (urgency) {
            case URGENCY_EMERGENCY -> 3;
            case URGENCY_URGENT -> 2;
            default -> 1;
        };
    }

    // Encapsulated private fields
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String urgency;

    /**
     * Full parameterized constructor.
     */
    public Appointment(String appointmentId, String patientId, String doctorId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this(appointmentId, patientId, doctorId, appointmentDate, appointmentTime, status, URGENCY_NORMAL);
    }

    /**
     * Full constructor including urgency.
     */
    public Appointment(String appointmentId, String patientId, String doctorId,
                       LocalDate appointmentDate, LocalTime appointmentTime,
                       String status, String urgency) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.urgency = urgency;
    }

    /** Default constructor for serialization. */
    public Appointment() {
    }

    /** Backward-compatible deserialization for urgency field. */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (urgency == null) {
            urgency = URGENCY_NORMAL;
        }
    }

    // --- Getters and Setters (Encapsulation) ---

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrgency() {
        return urgency != null ? urgency : URGENCY_NORMAL;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public String toString() {
        return "Appointment{" + "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", date=" + appointmentDate +
                ", time=" + appointmentTime +
                ", status='" + status + '\'' +
                ", urgency='" + urgency + '\'' + '}';
    }
}
