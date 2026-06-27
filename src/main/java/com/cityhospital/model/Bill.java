package com.cityhospital.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Bill represents an invoice generated for a patient appointment.
 * 
 * ENCAPSULATION: All fields private, accessed via getters/setters.
 * Implements Serializable for file-based persistence.
 * 
 * Total amount is calculated as doctorFee + medicineCharges + labCharges.
 */
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PAID = "Paid";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_PENDING = "Pending";

    private String billId;
    private String appointmentId;
    private String patientId;
    private String patientName;
    private double doctorFee;
    private double medicineCharges;
    private double labCharges;
    private String paymentStatus;
    private LocalDate billDate;

    public Bill() {}

    public Bill(String billId, String appointmentId, String patientId, String patientName,
                double doctorFee, double medicineCharges, double labCharges,
                String paymentStatus, LocalDate billDate) {
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorFee = doctorFee;
        this.medicineCharges = medicineCharges;
        this.labCharges = labCharges;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    /** Calculates the total bill amount. */
    public double getTotalAmount() {
        return doctorFee + medicineCharges + labCharges;
    }

    // Getters and Setters

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public double getDoctorFee() { return doctorFee; }
    public void setDoctorFee(double doctorFee) { this.doctorFee = doctorFee; }

    public double getMedicineCharges() { return medicineCharges; }
    public void setMedicineCharges(double medicineCharges) { this.medicineCharges = medicineCharges; }

    public double getLabCharges() { return labCharges; }
    public void setLabCharges(double labCharges) { this.labCharges = labCharges; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
}
