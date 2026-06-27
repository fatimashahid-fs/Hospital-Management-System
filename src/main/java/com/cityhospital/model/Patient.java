package com.cityhospital.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * INHERITANCE (OOP Concept):
 * Patient extends Person, inheriting all fields (id, name, phone, email)
 * and methods (getters/setters) from the parent class.
 * Patient IS-A Person.
 * 
 * ENCAPSULATION (OOP Concept):
 * Additional fields specific to Patient are kept private.
 */
public class Patient extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private String patientId;
    private int age;
    private String gender;
    private String medicalHistory;
    private String bloodGroup;

    public Patient(String id, String name, String phone, String email,
                   String patientId, int age, String gender, String medicalHistory) {
        this(id, name, phone, email, patientId, age, gender, medicalHistory, "");
    }

    public Patient(String id, String name, String phone, String email,
                   String patientId, int age, String gender, String medicalHistory,
                   String bloodGroup) {
        super(id, name, phone, email);
        this.patientId = patientId; this.age = age; this.gender = gender;
        this.medicalHistory = medicalHistory; this.bloodGroup = bloodGroup;
    }

    public Patient() { super(); }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (bloodGroup == null) bloodGroup = "";
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public String getBloodGroup() { return bloodGroup != null ? bloodGroup : ""; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    /**
     * METHOD OVERRIDING (OOP Concept):
     * Overrides the abstract displayInfo() method from Person.
     * This is RUNTIME POLYMORPHISM - the JVM decides which displayInfo() to call.
     */
    @Override
    public void displayInfo() {
        System.out.println("=== Patient Information ===");
        System.out.println("Patient ID: " + patientId);
        System.out.println("Name: " + getName());
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Blood Group: " + getBloodGroup());
        System.out.println("Phone: " + getPhone());
        System.out.println("Email: " + getEmail());
        System.out.println("Medical History: " + medicalHistory);
    }

    /**
     * METHOD OVERLOADING (OOP Concept):
     * Overloaded version of displayInfo that accepts a prefix parameter.
     * Same method name, different parameter list - compile-time polymorphism.
     */
    public void displayInfo(String prefix) {
        System.out.println(prefix + " Patient: " + getName());
    }
}
