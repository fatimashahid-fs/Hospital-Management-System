package com.cityhospital.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * VALIDATION SERVICE:
 * Provides static validation methods used across the application.
 * All user input is validated before being saved to the system.
 * 
 * This ensures data integrity and prevents invalid data from entering the system.
 */
public class ValidationService {

    /**
     * Validates a name field.
     * Rules: Required, 2-50 characters, alphabets and spaces only.
     * 
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (name.trim().length() < 2 || name.trim().length() > 50) {
            return false;
        }
        // Alphabets and spaces only
        return name.matches("[a-zA-Z ]+");
    }

    /**
     * Validates a phone number.
     * Rules: Exactly 11 digits, numeric only, must start with 03.
     * 
     * Valid examples: 03001234567, 03111234567
     * Invalid examples: 3001234567, 0300-1234567, +923001234567
     * 
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Must be exactly 11 digits and start with 03
        return phone.matches("03\\d{9}");
    }

    /**
     * Validates an email address.
     * Rules: Must contain @ and ., must follow standard email format.
     * 
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Basic email format: something@something.something
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates an age value.
     * Rules: Numeric, between 0 and 120 inclusive.
     * 
     * @param ageStr The age as a string
     * @return true if valid, false otherwise
     */
    public static boolean isValidAge(String ageStr) {
        if (ageStr == null || ageStr.trim().isEmpty()) {
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr.trim());
            return age >= 0 && age <= 120;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Extracts the integer value from an age string.
     * 
     * @param ageStr The age string
     * @return The age as int, or -1 if invalid
     */
    public static int parseAge(String ageStr) {
        try {
            return Integer.parseInt(ageStr.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Validates a date string.
     * Rules: Must be a valid date in yyyy-MM-dd format.
     * Date cannot be in the past.
     * 
     * @param dateStr The date string to validate
     * @return true if valid and not in the past, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr.trim(), formatter);
            // Date cannot be in the past
            return !date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates a time string or range.
     * Accepts HH:mm or HH:mm-HH:mm (range).
     * 
     * @param timeStr The time string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return false;
        }
        String[] parts = timeStr.trim().split("-");
        for (String part : parts) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime.parse(part.trim(), formatter);
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return parts.length >= 1 && parts.length <= 2;
    }

    /**
     * Validates medical history text.
     * Rules: Maximum 500 characters.
     * 
     * @param history The medical history text
     * @return true if valid (or empty), false if exceeds limit
     */
    public static boolean isValidMedicalHistory(String history) {
        if (history == null || history.trim().isEmpty()) {
            return true; // Medical history is optional
        }
        return history.trim().length() <= 500;
    }

    /**
     * Validates specialization.
     * Rules: Cannot be empty.
     * 
     * @param specialization The specialization text
     * @return true if valid, false otherwise
     */
    public static boolean isValidSpecialization(String specialization) {
        return specialization != null && !specialization.trim().isEmpty();
    }
}
