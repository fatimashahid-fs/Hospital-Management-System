package com.cityhospital.controller;

import com.cityhospital.model.Bill;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BillingController {

    public boolean createBill(String appointmentId, String patientId, String patientName,
                              double doctorFee, double medicineCharges, double labCharges, String paymentStatus) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) { AlertUtil.showError("Error", "Select an appointment."); return false; }
        if (doctorFee < 0 || medicineCharges < 0 || labCharges < 0) { AlertUtil.showError("Error", "Charges cannot be negative."); return false; }
        if (paymentStatus == null) { AlertUtil.showError("Error", "Select payment status."); return false; }

        String billId = IDGenerator.generateBillId();
        Bill bill = new Bill(billId, appointmentId.trim(), patientId, patientName,
                             doctorFee, medicineCharges, labCharges, paymentStatus, LocalDate.now());
        List<Bill> bills = FileManager.loadBills();
        bills.add(bill);
        FileManager.saveBills(bills);
        AlertUtil.showInfo("Success", "Invoice generated.\nBill ID: " + billId + "\nTotal: Rs. " + String.format("%.2f", bill.getTotalAmount()));
        return true;
    }

    public boolean updatePaymentStatus(String billId, String newStatus) {
        List<Bill> bills = FileManager.loadBills();
        for (Bill b : bills) {
            if (b.getBillId().equals(billId)) {
                b.setPaymentStatus(newStatus);
                FileManager.saveBills(bills);
                AlertUtil.showInfo("Success", "Payment status: " + newStatus);
                return true;
            }
        }
        AlertUtil.showError("Error", "Bill not found.");
        return false;
    }

    public List<Bill> searchBills(String query) {
        if (query == null || query.trim().isEmpty()) return FileManager.loadBills();
        String q = query.toLowerCase();
        return FileManager.loadBills().stream()
                .filter(b -> b.getBillId().toLowerCase().contains(q)
                        || b.getPatientName().toLowerCase().contains(q)
                        || b.getPatientId().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Bill> getAllBills() { return FileManager.loadBills(); }

    public List<Bill> getBillsByPatient(String patientId) {
        return FileManager.loadBills().stream()
                .filter(b -> b.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
}
