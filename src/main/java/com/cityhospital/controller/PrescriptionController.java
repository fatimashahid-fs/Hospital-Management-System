package com.cityhospital.controller;

import com.cityhospital.model.Prescription;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionController {

    public List<Prescription> getAllPrescriptions() {
        return FileManager.loadPrescriptions();
    }

    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        return FileManager.loadPrescriptions().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .sorted((a, b) -> b.getDatePrescribed().compareTo(a.getDatePrescribed()))
                .collect(Collectors.toList());
    }

    public List<Prescription> getPrescriptionsByDoctor(String doctorId) {
        return FileManager.loadPrescriptions().stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .sorted((a, b) -> b.getDatePrescribed().compareTo(a.getDatePrescribed()))
                .collect(Collectors.toList());
    }

    public void addPrescription(String patientId, String doctorId,
                                String medicineName, String dosage, String instructions) {
        List<Prescription> list = FileManager.loadPrescriptions();
        String id = IDGenerator.generatePrescriptionId();
        list.add(new Prescription(id, patientId, doctorId, medicineName, dosage, instructions));
        FileManager.savePrescriptions(list);
    }
}
