package com.cityhospital.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;

    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private String medicineName;
    private String dosage;
    private String instructions;
    private LocalDate datePrescribed;

    public Prescription(String prescriptionId, String patientId, String doctorId,
                        String medicineName, String dosage, String instructions) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.instructions = instructions;
        this.datePrescribed = LocalDate.now();
    }

    public Prescription() {}

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDate getDatePrescribed() { return datePrescribed; }
    public void setDatePrescribed(LocalDate datePrescribed) { this.datePrescribed = datePrescribed; }
}
