package com.cityhospital.controller;

import com.cityhospital.model.Doctor;
import com.cityhospital.service.FileManager;
import com.cityhospital.service.ValidationService;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorController {

    public boolean addDoctor(String id, String name, String phone, String email,
                              String specialization, String availableTime) {
        if (!ValidationService.isValidName(name)) { AlertUtil.showError("Error", "Invalid name."); return false; }
        if (!ValidationService.isValidPhone(phone)) { AlertUtil.showError("Error", "Invalid phone."); return false; }
        if (!ValidationService.isValidEmail(email)) { AlertUtil.showError("Error", "Invalid email."); return false; }
        if (!ValidationService.isValidSpecialization(specialization)) { AlertUtil.showError("Error", "Specialization required."); return false; }
        if (!ValidationService.isValidTime(availableTime)) { AlertUtil.showError("Error", "Invalid time."); return false; }

        String doctorId = IDGenerator.generateDoctorId();
        Doctor doctor = new Doctor(id, name.trim(), phone.trim(), email.trim(),
                                    doctorId, specialization.trim(), availableTime.trim());
        List<Doctor> doctors = FileManager.loadDoctors();
        doctors.add(doctor);
        FileManager.saveDoctors(doctors);
        AlertUtil.showInfo("Success", "Doctor added.\nID: " + doctorId);
        return true;
    }

    public boolean updateDoctor(Doctor doctor) {
        if (doctor == null) { AlertUtil.showError("Error", "No doctor selected."); return false; }
        List<Doctor> doctors = FileManager.loadDoctors();
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getDoctorId().equals(doctor.getDoctorId())) {
                doctors.set(i, doctor);
                FileManager.saveDoctors(doctors);
                AlertUtil.showInfo("Success", "Doctor updated.");
                return true;
            }
        }
        AlertUtil.showError("Error", "Doctor not found.");
        return false;
    }

    public boolean deleteDoctor(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) { AlertUtil.showError("Error", "Select a doctor."); return false; }
        List<Doctor> doctors = FileManager.loadDoctors();
        if (doctors.removeIf(d -> d.getDoctorId().equals(doctorId))) {
            FileManager.saveDoctors(doctors);
            AlertUtil.showInfo("Success", "Doctor deleted.");
            return true;
        }
        AlertUtil.showError("Error", "Doctor not found.");
        return false;
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) return FileManager.loadDoctors();
        return FileManager.loadDoctors().stream()
                .filter(d -> d.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Doctor> getAllDoctors() { return FileManager.loadDoctors(); }

    public Doctor getDoctorById(String doctorId) {
        return FileManager.loadDoctors().stream()
                .filter(d -> d.getDoctorId().equals(doctorId))
                .findFirst().orElse(null);
    }
}
