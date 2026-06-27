package com.cityhospital.service;

import com.cityhospital.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DemoDataService populates the system with sample data on first run.
 * This allows users to immediately explore features without manual data entry.
 * 
 * Demo data is loaded only once — when the doctors list is empty (first run).
 */
public class DemoDataService {

    /**
     * Seeds the system with demo patients, doctors, and appointments.
     * Called once at application startup if no doctors exist yet.
     */
    public static void seedDemoData() {
        List<Doctor> existingDoctors = FileManager.loadDoctors();
        if (!existingDoctors.isEmpty()) {
            return; // Demo data already loaded
        }

        seedDoctors();
        seedPatients();
        linkDemoPatientUser();
        seedAppointments();
        seedEmergencyCases();
        seedBills();
        seedMedicines();
        seedPrescriptions();
        seedFeedback();

        System.out.println("Demo data loaded successfully.");
    }

    public static void supplementDemoData() {
        supplementAppointments();
        supplementPatients();
        supplementDoctors();
        supplementPrescriptions();
        supplementFeedback();
        supplementBills();
        supplementNotifications();
        supplementEmergencyCases();
        System.out.println("Supplemental demo data loaded.");
    }

    // ============================================================
    // DEMO DOCTORS
    // ============================================================
    private static void seedDoctors() {
        List<Doctor> doctors = FileManager.loadDoctors();

        doctors.add(new Doctor("DOC001", "Ahmed Khan", "03001112221",
                "ahmed.khan@cityhospital.com", "DOC001",
                "Cardiologist", "09:00 - 13:00"));

        doctors.add(new Doctor("DOC002", "Sarah Ali", "03001112222",
                "sarah.ali@cityhospital.com", "DOC002",
                "Neurologist", "10:00 - 16:00"));

        doctors.add(new Doctor("DOC003", "Usman Malik", "03001112223",
                "usman.malik@cityhospital.com", "DOC003",
                "Orthopedic Surgeon", "09:00 - 15:00"));

        doctors.add(new Doctor("DOC004", "Fatima Zaidi", "03001112224",
                "fatima.zaidi@cityhospital.com", "DOC004",
                "Pediatrician", "11:00 - 17:00"));

        doctors.add(new Doctor("DOC005", "Omar Farooq", "03001112225",
                "omar.farooq@cityhospital.com", "DOC005",
                "Dermatologist", "09:00 - 14:00"));

        FileManager.saveDoctors(doctors);
    }

    // ============================================================
    // DEMO PATIENTS
    // ============================================================
    private static void seedPatients() {
        List<Patient> patients = FileManager.loadPatients();

        patients.add(new Patient("PAT001", "Ali Raza", "03001234501",
                "ali.raza@email.com", "PAT001",
                30, "Male", "No known conditions", "A+"));
        patients.add(new Patient("PAT002", "Sana Tariq", "03001234502",
                "sana.tariq@email.com", "PAT002",
                25, "Female", "Asthma", "B+"));
        patients.add(new Patient("PAT003", "Bilal Ahmed", "03001234503",
                "bilal.ahmed@email.com", "PAT003",
                45, "Male", "Diabetes Type 2", "O+"));
        patients.add(new Patient("PAT004", "Ayesha Khan", "03001234504",
                "ayesha.khan@email.com", "PAT004",
                35, "Female", "Migraine", "AB-"));
        patients.add(new Patient("PAT005", "Zain Abbas", "03001234505",
                "patient@email.com", "PAT005",
                50, "Male", "Hypertension", "O-"));

        FileManager.savePatients(patients);
    }

    // ============================================================
    // LINK DEMO PATIENT USER
    // ============================================================
    private static void linkDemoPatientUser() {
        List<User> users = FileManager.loadUsers();
        for (User u : users) {
            if ("patient".equals(u.getUsername())) {
                if (u.getPatientId() == null) u.setPatientId("PAT005");
                if (u.getPatientEmail() == null) u.setPatientEmail("patient@email.com");
                break;
            }
        }
        FileManager.saveUsers(users);
        PatientUserMapper.link("patient", "PAT005");
    }

    // ============================================================
    // DEMO APPOINTMENTS
    // ============================================================
    private static void seedAppointments() {
        List<Appointment> appointments = FileManager.loadAppointments();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        // Completed appointments (yesterday)
        appointments.add(new Appointment("APT001", "PAT001", "DOC001",
                yesterday, LocalTime.of(10, 0), Appointment.STATUS_COMPLETED));
        appointments.add(new Appointment("APT002", "PAT002", "DOC002",
                yesterday, LocalTime.of(11, 0), Appointment.STATUS_COMPLETED));
        appointments.add(new Appointment("APT003", "PAT003", "DOC003",
                yesterday, LocalTime.of(14, 0), Appointment.STATUS_COMPLETED));

        // Scheduled appointments (today and tomorrow) with mixed urgency
        appointments.add(new Appointment("APT004", "PAT001", "DOC004",
                today, LocalTime.of(10, 30), Appointment.STATUS_SCHEDULED, Appointment.URGENCY_EMERGENCY));
        appointments.add(new Appointment("APT005", "PAT004", "DOC001",
                today, LocalTime.of(11, 0), Appointment.STATUS_SCHEDULED, Appointment.URGENCY_URGENT));
        appointments.add(new Appointment("APT006", "PAT002", "DOC003",
                today, LocalTime.of(15, 0), Appointment.STATUS_SCHEDULED));
        appointments.add(new Appointment("APT007", "PAT005", "DOC005",
                tomorrow, LocalTime.of(9, 0), Appointment.STATUS_SCHEDULED, Appointment.URGENCY_URGENT));
        appointments.add(new Appointment("APT008", "PAT003", "DOC002",
                tomorrow, LocalTime.of(13, 0), Appointment.STATUS_SCHEDULED));

        // One cancelled for demo
        appointments.add(new Appointment("APT009", "PAT004", "DOC004",
                tomorrow, LocalTime.of(16, 0), Appointment.STATUS_CANCELLED));

        FileManager.saveAppointments(appointments);
    }

    // ============================================================
    // DEMO EMERGENCY CASES
    // ============================================================
    private static void seedEmergencyCases() {
        List<EmergencyCase> cases = FileManager.loadEmergencyCases();

        cases.add(new EmergencyCase("EMC001", "PAT001", "DOC004",
                EmergencyCase.URGENCY_EMERGENCY, "Severe chest pain - possible heart attack", "APT004"));
        cases.add(new EmergencyCase("EMC002", "PAT004", "DOC001",
                EmergencyCase.URGENCY_URGENT, "High fever with severe headache", "APT005"));
        cases.add(new EmergencyCase("EMC003", "PAT005", "DOC005",
                EmergencyCase.URGENCY_URGENT, "Skin rash with breathing difficulty", "APT007"));

        FileManager.saveEmergencyCases(cases);
    }

    // ============================================================
    // DEMO BILLS
    // ============================================================
    private static void seedBills() {
        List<Bill> bills = FileManager.loadBills();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        bills.add(new Bill("BIL001", "APT001", "PAT001", "Ali Raza",
                1500, 800, 0, Bill.STATUS_PAID, yesterday));
        bills.add(new Bill("BIL002", "APT002", "PAT002", "Sana Tariq",
                2000, 1200, 500, Bill.STATUS_PAID, yesterday));
        bills.add(new Bill("BIL003", "APT003", "PAT003", "Bilal Ahmed",
                2500, 600, 200, Bill.STATUS_UNPAID, yesterday));

        FileManager.saveBills(bills);
    }

    // ============================================================
    // DEMO MEDICINES
    // ============================================================
    private static void seedMedicines() {
        List<Medicine> meds = FileManager.loadMedicines();

        meds.add(new Medicine("MED001", "Paracetamol", "Tablet", 15.0, 500, "PharmaCo"));
        meds.add(new Medicine("MED002", "Amoxicillin", "Capsule", 50.0, 200, "MediCare Labs"));
        meds.add(new Medicine("MED003", "Ibuprofen", "Tablet", 20.0, 300, "HealthPharm"));
        meds.add(new Medicine("MED004", "Omeprazole", "Capsule", 35.0, 150, "GastroMed"));
        meds.add(new Medicine("MED005", "Cetirizine", "Tablet", 8.0, 400, "AllerPharm"));
        meds.add(new Medicine("MED006", "Salbutamol Inhaler", "Inhaler", 250.0, 80, "RespirCare"));
        meds.add(new Medicine("MED007", "Amoxiclav", "Tablet", 120.0, 100, "MediCare Labs"));
        meds.add(new Medicine("MED008", "ORS Powder", "Other", 25.0, 250, "HealthPharm"));

        FileManager.saveMedicines(meds);
    }

    // ============================================================
    // DEMO PRESCRIPTIONS
    // ============================================================
    private static void seedPrescriptions() {
        List<Prescription> list = FileManager.loadPrescriptions();
        list.add(new Prescription("PRX001", "PAT001", "DOC001", "Paracetamol", "500mg", "Take 1 tablet twice daily after meals"));
        list.add(new Prescription("PRX002", "PAT001", "DOC001", "Amoxicillin", "250mg", "Take 1 capsule three times daily"));
        list.add(new Prescription("PRX003", "PAT002", "DOC002", "Ibuprofen", "400mg", "Take 1 tablet as needed for pain"));
        list.add(new Prescription("PRX004", "PAT004", "DOC001", "Omeprazole", "20mg", "Take 1 capsule before breakfast"));
        FileManager.savePrescriptions(list);
    }

    // ============================================================
    // DEMO FEEDBACK
    // ============================================================
    private static void seedFeedback() {
        List<Feedback> list = FileManager.loadFeedback();
        list.add(new Feedback("FDB001", "PAT001", "Ali Raza", 5, "Excellent service! The staff was very helpful."));
        list.add(new Feedback("FDB002", "PAT002", "Sana Tariq", 4, "Good experience overall. Wait time was reasonable."));
        list.add(new Feedback("FDB003", "PAT003", "Bilal Ahmed", 3, "Doctor was good but reception could be better."));
        FileManager.saveFeedback(list);
    }

    // ============================================================
    // SUPPLEMENTAL DATA (fills gaps without overwriting)
    // ============================================================

    private static void supplementAppointments() {
        List<Appointment> list = FileManager.loadAppointments();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Only add if no pending appointments exist
        boolean hasPending = list.stream().anyMatch(a -> Appointment.STATUS_PENDING.equals(a.getStatus()));
        if (!hasPending) {
            list.add(new Appointment("APT010", "PAT005", "DOC001", today, LocalTime.of(14, 0), Appointment.STATUS_PENDING, Appointment.URGENCY_EMERGENCY));
            list.add(new Appointment("APT011", "PAT004", "DOC003", today, LocalTime.of(16, 0), Appointment.STATUS_PENDING));
            list.add(new Appointment("APT012", "PAT002", "DOC005", tomorrow, LocalTime.of(10, 0), Appointment.STATUS_PENDING, Appointment.URGENCY_URGENT));
            list.add(new Appointment("APT013", "PAT003", "DOC004", tomorrow, LocalTime.of(15, 0), Appointment.STATUS_PENDING));
            System.out.println("  Added 4 pending appointments.");
        }
        FileManager.saveAppointments(list);
    }

    private static void supplementPatients() {
        List<Patient> list = FileManager.loadPatients();
        if (list.stream().noneMatch(p -> "PAT006".equals(p.getPatientId()))) {
            list.add(new Patient("PAT006", "Hina Tariq", "03001234506", "hina.tariq@email.com", "PAT006", 28, "Female", "Allergic to penicillin", "B-"));
            list.add(new Patient("PAT007", "Kamran Ali", "03001234507", "kamran.ali@email.com", "PAT007", 55, "Male", "Heart disease, takes blood thinners", "A+"));
            list.add(new Patient("PAT008", "Nadia Hussain", "03001234508", "nadia.hussain@email.com", "PAT008", 32, "Female", "Pregnancy checkup - 2nd trimester", "O+"));
            System.out.println("  Added 3 patients.");
        }
        FileManager.savePatients(list);
    }

    private static void supplementDoctors() {
        List<Doctor> list = FileManager.loadDoctors();
        if (list.stream().noneMatch(d -> "DOC006".equals(d.getDoctorId()))) {
            list.add(new Doctor("DOC006", "Hassan Rizvi", "03001112226", "hassan.rizvi@cityhospital.com", "DOC006", "General Physician", "08:00 - 12:00"));
            list.add(new Doctor("DOC007", "Amina Tariq", "03001112227", "amina.tariq@cityhospital.com", "DOC007", "Gynecologist", "10:00 - 18:00"));
            System.out.println("  Added 2 doctors.");
        }
        FileManager.saveDoctors(list);
    }

    private static void supplementPrescriptions() {
        List<Prescription> list = FileManager.loadPrescriptions();
        if (list.stream().noneMatch(p -> "PRX005".equals(p.getPrescriptionId()))) {
            list.add(new Prescription("PRX005", "PAT003", "DOC003", "Diclofenac Gel", "Apply topically", "Apply to affected area 3 times daily for pain relief"));
            list.add(new Prescription("PRX006", "PAT005", "DOC005", "Loratadine", "10mg", "Take 1 tablet daily for allergy relief"));
            list.add(new Prescription("PRX007", "PAT002", "DOC002", "Sumatriptan", "50mg", "Take at onset of migraine, max 2 per day"));
            list.add(new Prescription("PRX008", "PAT004", "DOC001", "Atorvastatin", "20mg", "Take 1 tablet at bedtime for cholesterol"));
            System.out.println("  Added 4 prescriptions.");
        }
        FileManager.savePrescriptions(list);
    }

    private static void supplementFeedback() {
        List<Feedback> list = FileManager.loadFeedback();
        if (list.stream().noneMatch(f -> "FDB004".equals(f.getFeedbackId()))) {
            list.add(new Feedback("FDB004", "PAT004", "Ayesha Khan", 5, "Dr. Sarah Ali was very thorough and caring. Highly recommended!"));
            list.add(new Feedback("FDB005", "PAT005", "Zain Abbas", 2, "Wait time was too long despite having an appointment."));
            System.out.println("  Added 2 feedback entries.");
        }
        FileManager.saveFeedback(list);
    }

    private static void supplementBills() {
        List<Bill> list = FileManager.loadBills();
        if (list.stream().noneMatch(b -> "BIL004".equals(b.getBillId()))) {
            LocalDate today = LocalDate.now();
            list.add(new Bill("BIL004", "APT004", "PAT001", "Ali Raza", 3000, 500, 1200, Bill.STATUS_UNPAID, today));
            list.add(new Bill("BIL005", "APT005", "PAT004", "Ayesha Khan", 2500, 800, 600, Bill.STATUS_PENDING, today));
            list.add(new Bill("BIL006", "APT006", "PAT002", "Sana Tariq", 1800, 0, 400, Bill.STATUS_UNPAID, today));
            System.out.println("  Added 3 bills.");
        }
        FileManager.saveBills(list);
    }

    private static void supplementEmergencyCases() {
        List<EmergencyCase> list = FileManager.loadEmergencyCases();
        if (list.stream().noneMatch(c -> "EMC004".equals(c.getCaseId()))) {
            list.add(new EmergencyCase("EMC004", "PAT006", "DOC006", EmergencyCase.URGENCY_URGENT, "Severe allergic reaction - swelling and hives", "APT010"));
            list.add(new EmergencyCase("EMC005", "PAT003", "DOC004", EmergencyCase.URGENCY_EMERGENCY, "Pediatric emergency - high fever with seizures", "APT013"));
            System.out.println("  Added 2 emergency cases.");
        }
        FileManager.saveEmergencyCases(list);
    }

    private static void supplementNotifications() {
        List<NotificationRecord> list = FileManager.loadNotifications();
        if (list.stream().noneMatch(n -> "NTF001".equals(n.getId()))) {
            LocalDate d = LocalDate.now();
            list.add(new NotificationRecord("NTF001", NotificationRecord.TYPE_ALERT, "", "System", "Welcome to THE CITY HOSPITAL Management System", "Sent", d.atStartOfDay()));
            list.add(new NotificationRecord("NTF002", NotificationRecord.TYPE_EMAIL, "ali.raza@email.com", "Appointment", "Your appointment APT004 has been scheduled for " + d + " at 10:30", "Sent", d.atTime(9, 0)));
            list.add(new NotificationRecord("NTF003", NotificationRecord.TYPE_EMAIL, "sana.tariq@email.com", "Appointment", "Your appointment APT006 has been completed.", "Sent", d.minusDays(1).atTime(16, 0)));
            list.add(new NotificationRecord("NTF004", NotificationRecord.TYPE_SMS, "03001234505", "Reminder", "Reminder: You have an appointment tomorrow at 09:00 with Dr. Omar Farooq.", "Sent", d.atTime(20, 0)));
            list.add(new NotificationRecord("NTF005", NotificationRecord.TYPE_ALERT, "", "Feedback", "New feedback submitted by Bilal Ahmed (rating: 3)", "Sent", d.minusDays(1).atTime(14, 30)));
            list.add(new NotificationRecord("NTF006", NotificationRecord.TYPE_EMAIL, "admin@cityhospital.com", "System", "Daily report: 8 appointments today, 3 pending approvals", "Sent", d.atTime(8, 0)));
            System.out.println("  Added 6 notification history entries.");
        }
        FileManager.saveNotifications(list);
    }
}
