package com.cityhospital.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * DateTimeUtil provides helper methods for parsing and formatting
 * dates and times used throughout the application.
 */
public class DateTimeUtil {

    /** Standard date formatter: yyyy-MM-dd */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** Standard time formatter: HH:mm */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Converts a date string (yyyy-MM-dd) to a LocalDate object.
     * 
     * @param dateStr The date string to parse
     * @return LocalDate if parsing succeeds, null otherwise
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats a LocalDate to a string (yyyy-MM-dd).
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Converts a time string (HH:mm) to a LocalTime object.
     * 
     * @param timeStr The time string to parse
     * @return LocalTime if parsing succeeds, null otherwise
     */
    public static LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats a LocalTime to a string (HH:mm).
     * 
     * @param time The time to format
     * @return Formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * Generates a list of time slot strings in HH:mm format
     * from 08:00 to 18:00 in 30-minute intervals.
     * Used for dropdown menus when booking appointments.
     * 
     * @return List of time slot strings
     */
    public static List<String> getTimeSlots() {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);
        while (!start.isAfter(end)) {
            slots.add(start.format(TIME_FORMATTER));
            start = start.plusMinutes(30);
        }
        return slots;
    }
}
