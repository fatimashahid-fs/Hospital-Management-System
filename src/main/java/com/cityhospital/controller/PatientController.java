package com.cityhospital.controller;

import com.cityhospital.model.Patient;
import com.cityhospital.service.FileManager;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class PatientController {

    public boolean addPatient(String id, String name, String phone, String email,
                              String ageStr, String gender, String medicalHistory) {
        return addPatient(id, name, phone, email, ageStr, gender, medicalHistory, "");
    }

    public boolean addPatient(String id, String name, String phone, String email,
                              String ageStr, String gender, String medicalHistory, String bloodGroup) {
        if (!ValidationService.isValidName(name)) { AlertUtil.showError("Error", "Invalid name."); return false; }
        if (!ValidationService.isValidPhone(phone)) { AlertUtil.showError("Error", "Invalid phone."); return false; }
        if (!ValidationService.isValidEmail(email)) { AlertUtil.showError("Error", "Invalid email."); return false; }
        if (!ValidationService.isValidAge(ageStr)) { AlertUtil.showError("Error", "Invalid age."); return false; }
        if (!ValidationService.isValidMedicalHistory(medicalHistory)) { AlertUtil.showError("Error", "Medical history too long."); return false; }

        String patientId = IDGenerator.generatePatientId();
        int age = ValidationService.parseAge(ageStr);

        Patient patient = new Patient(id, name.trim(), phone.trim(), email.trim(),
                patientId, age, gender, medicalHistory.trim(),
                bloodGroup != null ? bloodGroup.trim() : "");
        List<Patient> patients = FileManager.loadPatients();
        patients.add(patient);
        FileManager.savePatients(patients);
        AlertUtil.showInfo("Success", "Patient registered.\nID: " + patientId);
        return true;
    }

    public boolean updatePatient(Patient patient) {
        if (patient == null) { AlertUtil.showError("Error", "No patient selected."); return false; }
        List<Patient> patients = FileManager.loadPatients();
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equals(patient.getPatientId())) {
                patients.set(i, patient);
                FileManager.savePatients(patients);
                AlertUtil.showInfo("Success", "Patient updated.");
                return true;
            }
        }
        AlertUtil.showError("Error", "Patient not found.");
        return false;
    }

    public boolean deletePatient(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) { AlertUtil.showError("Error", "Select a patient."); return false; }
        List<Patient> patients = FileManager.loadPatients();
        if (patients.removeIf(p -> p.getPatientId().equals(patientId))) {
            FileManager.savePatients(patients);
            AlertUtil.showInfo("Success", "Patient deleted.");
            return true;
        }
        AlertUtil.showError("Error", "Patient not found.");
        return false;
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) return FileManager.loadPatients();
        return FileManager.loadPatients().stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Patient> getAllPatients() { return FileManager.loadPatients(); }

    public Patient getPatientById(String patientId) {
        return FileManager.loadPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst().orElse(null);
    }
}
