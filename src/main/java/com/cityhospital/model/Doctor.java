package com.cityhospital.model;

import java.io.Serializable;

/**
 * INHERITANCE (OOP Concept):
 * Doctor extends Person, inheriting all common person fields.
 * Doctor IS-A Person.
 * 
 * ENCAPSULATION (OOP Concept):
 * Doctor-specific fields are private, accessed through public getters/setters.
 */
public class Doctor extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Doctor-specific private fields
    private String doctorId;
    private String specialization;
    private String availableTime;

    /**
     * Full parameterized constructor.
     * 
     * @param id             Person ID
     * @param name           Doctor name
     * @param phone          Phone number
     * @param email          Email address
     * @param doctorId       Doctor-specific ID (e.g., DOC001)
     * @param specialization Doctor's specialization
     * @param availableTime  Available time slot
     */
    public Doctor(String id, String name, String phone, String email,
                  String doctorId, String specialization, String availableTime) {
        super(id, name, phone, email);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.availableTime = availableTime;
    }

    /** Default constructor for serialization. */
    public Doctor() {
        super();
    }

    // --- Getters and Setters ---

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    /**
     * METHOD OVERRIDING (OOP Concept):
     * Overrides displayInfo() from Person with Doctor-specific implementation.
     * Demonstrates POLYMORPHISM - the same method behaves differently
     * depending on whether it is called on a Patient or Doctor object.
     */
    @Override
    public void displayInfo() {
        System.out.println("=== Doctor Information ===");
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Name: " + getName());
        System.out.println("Specialization: " + specialization);
        System.out.println("Available Time: " + availableTime);
        System.out.println("Phone: " + getPhone());
        System.out.println("Email: " + getEmail());
    }

    /**
     * METHOD OVERLOADING (OOP Concept):
     * Overloaded displayInfo with a prefix parameter.
     */
    public void displayInfo(String prefix) {
        System.out.println(prefix + " Doctor: " + getName());
    }
}
