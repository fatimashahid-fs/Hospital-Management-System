package com.cityhospital.controller;

import com.cityhospital.model.Medicine;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PharmacyController manages medicine inventory — add, update, delete,
 * search, and stock management.
 */
public class PharmacyController {

    /**
     * Adds a new medicine to the inventory.
     */
    public boolean addMedicine(String name, String category, double price, int quantity, String manufacturer) {
        if (name == null || name.trim().isEmpty()) {
            AlertUtil.showError("Error", "Medicine name cannot be empty."); return false;
        }
        if (price < 0) {
            AlertUtil.showError("Error", "Price cannot be negative."); return false;
        }
        if (quantity < 0) {
            AlertUtil.showError("Error", "Quantity cannot be negative."); return false;
        }

        String medId = IDGenerator.generateMedicineId();
        Medicine med = new Medicine(medId, name.trim(), category, price, quantity,
                                    manufacturer != null ? manufacturer.trim() : "");

        List<Medicine> meds = FileManager.loadMedicines();
        meds.add(med);
        FileManager.saveMedicines(meds);
        AlertUtil.showInfo("Success", "Medicine added.\nID: " + medId);
        return true;
    }

    /**
     * Updates an existing medicine record.
     */
    public boolean updateMedicine(Medicine med) {
        if (med == null) { AlertUtil.showError("Error", "No medicine selected."); return false; }
        List<Medicine> meds = FileManager.loadMedicines();
        for (int i = 0; i < meds.size(); i++) {
            if (meds.get(i).getMedicineId().equals(med.getMedicineId())) {
                meds.set(i, med);
                FileManager.saveMedicines(meds);
                AlertUtil.showInfo("Success", "Medicine updated.");
                return true;
            }
        }
        AlertUtil.showError("Error", "Medicine not found.");
        return false;
    }

    /**
     * Deletes a medicine by ID.
     */
    public boolean deleteMedicine(String medId) {
        List<Medicine> meds = FileManager.loadMedicines();
        boolean removed = meds.removeIf(m -> m.getMedicineId().equals(medId));
        if (removed) {
            FileManager.saveMedicines(meds);
            AlertUtil.showInfo("Success", "Medicine deleted.");
            return true;
        }
        AlertUtil.showError("Error", "Medicine not found.");
        return false;
    }

    /**
     * Searches medicines by name or category.
     */
    public List<Medicine> searchMedicines(String query) {
        if (query == null || query.trim().isEmpty()) return FileManager.loadMedicines();
        String q = query.toLowerCase();
        return FileManager.loadMedicines().stream()
                .filter(m -> m.getName().toLowerCase().contains(q)
                        || m.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Medicine> getAllMedicines() { return FileManager.loadMedicines(); }
}
