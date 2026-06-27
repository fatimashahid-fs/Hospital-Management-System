package com.cityhospital.controller;

import com.cityhospital.model.EmergencyCase;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class EmergencyCaseController {

    public List<EmergencyCase> getAllCases() {
        return FileManager.loadEmergencyCases();
    }

    public List<EmergencyCase> getCasesByPatient(String patientId) {
        return FileManager.loadEmergencyCases().stream()
                .filter(c -> c.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<EmergencyCase> getCasesByDoctor(String doctorId) {
        return FileManager.loadEmergencyCases().stream()
                .filter(c -> c.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<EmergencyCase> getOpenCases() {
        return FileManager.loadEmergencyCases().stream()
                .filter(c -> EmergencyCase.STATUS_OPEN.equals(c.getStatus())
                        || EmergencyCase.STATUS_IN_PROGRESS.equals(c.getStatus()))
                .sorted((a, b) -> {
                    if (EmergencyCase.URGENCY_EMERGENCY.equals(a.getUrgency())
                            && !EmergencyCase.URGENCY_EMERGENCY.equals(b.getUrgency())) return -1;
                    if (!EmergencyCase.URGENCY_EMERGENCY.equals(a.getUrgency())
                            && EmergencyCase.URGENCY_EMERGENCY.equals(b.getUrgency())) return 1;
                    return a.getReportedAt().compareTo(b.getReportedAt());
                })
                .collect(Collectors.toList());
    }

    public EmergencyCase createCase(String patientId, String doctorId,
                                     String urgency, String description, String appointmentId) {
        List<EmergencyCase> cases = FileManager.loadEmergencyCases();
        String caseId = IDGenerator.generateEmergencyCaseId();
        EmergencyCase ec = new EmergencyCase(caseId, patientId, doctorId, urgency, description, appointmentId);
        cases.add(ec);
        FileManager.saveEmergencyCases(cases);
        return ec;
    }

    public void updateStatus(String caseId, String newStatus) {
        List<EmergencyCase> cases = FileManager.loadEmergencyCases();
        cases.stream()
                .filter(c -> c.getCaseId().equals(caseId))
                .findFirst()
                .ifPresent(c -> { c.setStatus(newStatus); });
        FileManager.saveEmergencyCases(cases);
    }
}
