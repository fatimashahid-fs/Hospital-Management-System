package com.cityhospital.model;

import java.io.Serializable;

/**
 * ABSTRACTION (OOP Concept):
 * Person is an abstract class that provides a blueprint for all person types.
 * It cannot be instantiated directly - only its subclasses (Patient, Doctor) can.
 * 
 * ENCAPSULATION (OOP Concept):
 * All fields are private. Access is provided only through public getters and setters.
 * This protects the internal state from unauthorized direct access.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String phone;
    private String email;

    public Person(String id, String name, String phone, String email) {
        this.id = id; this.name = name; this.phone = phone; this.email = email;
    }

    public Person() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * ABSTRACT METHOD (OOP Concept):
     * Declared here with no body. Each subclass MUST override this method.
     */
    public abstract void displayInfo();
}
