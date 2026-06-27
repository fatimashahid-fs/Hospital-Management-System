package com.cityhospital.service;

import java.io.*;
import java.util.Properties;

public class PatientUserMapper {

    private static final String MAP_FILE = "data/patient_user_map.properties";

    private static Properties loadMap() {
        Properties props = new Properties();
        File f = new File(MAP_FILE);
        if (f.exists()) {
            try (InputStream in = new FileInputStream(f)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Error loading patient-user map: " + e.getMessage());
            }
        }
        return props;
    }

    private static void saveMap(Properties props) {
        try {
            new File("data").mkdirs();
            try (OutputStream out = new FileOutputStream(MAP_FILE)) {
                props.store(out, "Patient-User mapping (username=patientId)");
            }
        } catch (IOException e) {
            System.err.println("Error saving patient-user map: " + e.getMessage());
        }
    }

    public static void link(String username, String patientId) {
        Properties props = loadMap();
        props.setProperty(username.toLowerCase(), patientId);
        saveMap(props);
    }

    public static String getPatientId(String username) {
        if (username == null) return null;
        return loadMap().getProperty(username.toLowerCase());
    }
}
