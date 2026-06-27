package com.cityhospital.model;

import java.io.Serializable;

/**
 * Medicine represents a pharmaceutical item in the hospital pharmacy inventory.
 * 
 * ENCAPSULATION: All fields private, accessed via getters/setters.
 * Implements Serializable for file-based persistence.
 */
public class Medicine implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineId;
    private String name;
    private String category;   // Tablet, Syrup, Injection, Cream, etc.
    private double price;      // Price per unit
    private int quantity;      // Current stock quantity
    private String manufacturer;

    public Medicine() {}

    public Medicine(String medicineId, String name, String category,
                    double price, int quantity, String manufacturer) {
        this.medicineId = medicineId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.manufacturer = manufacturer;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}
