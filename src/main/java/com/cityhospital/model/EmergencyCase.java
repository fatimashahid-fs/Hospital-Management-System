package com.cityhospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmergencyCase implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_TREATED = "Treated";

    public static final String URGENCY_EMERGENCY = "Emergency";
    public static final String URGENCY_URGENT = "Urgent";

    private String caseId;
    private String patientId;
    private String doctorId;
    private String urgency;
    private String description;
    private LocalDateTime reportedAt;
    private String status;
    private String appointmentId;

    public EmergencyCase(String caseId, String patientId, String doctorId,
                         String urgency, String description, String appointmentId) {
        this.caseId = caseId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.urgency = urgency;
        this.description = description;
        this.reportedAt = LocalDateTime.now();
        this.status = STATUS_OPEN;
        this.appointmentId = appointmentId;
    }

    public EmergencyCase() {}

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
}
