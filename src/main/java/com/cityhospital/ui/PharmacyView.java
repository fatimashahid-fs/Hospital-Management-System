package com.cityhospital.ui;

import com.cityhospital.controller.PharmacyController;
import com.cityhospital.model.Medicine;
import com.cityhospital.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * PharmacyView — Medicine inventory management UI.
 */
public class PharmacyView {

    private final PharmacyController controller = new PharmacyController();
    private TableView<Medicine> table;
    private TextField fName, fPrice, fQty, fMfr;
    private ComboBox<String> fCategory;

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("PHARMACY / MEDICINE INVENTORY");
        title.getStyleClass().add("page-title");

        // Search
        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by name or category...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        TableColumn<Medicine, String> cId = new TableColumn<>("Med ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        TableColumn<Medicine, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Medicine, String> cCat = new TableColumn<>("Category");
        cCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Medicine, Double> cPrice = new TableColumn<>("Price (Rs)");
        cPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Medicine, Integer> cQty = new TableColumn<>("Stock");
        cQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Medicine, String> cMfr = new TableColumn<>("Manufacturer");
        cMfr.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        table.getColumns().addAll(cId, cName, cCat, cPrice, cQty, cMfr);
        refreshTable();

        // Form
        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));

        fName = new TextField(); fName.setPromptText("Medicine name"); fName.getStyleClass().add("text-field");
        fCategory = new ComboBox<>();
        fCategory.getItems().addAll("Tablet", "Syrup", "Injection", "Cream", "Drop", "Inhaler", "Capsule", "Other");
        fCategory.setPromptText("Category"); fCategory.getStyleClass().add("combo-box");
        fPrice = new TextField(); fPrice.setPromptText("Price per unit"); fPrice.getStyleClass().add("text-field");
        fQty = new TextField(); fQty.setPromptText("Stock quantity"); fQty.getStyleClass().add("text-field");
        fMfr = new TextField(); fMfr.setPromptText("Manufacturer"); fMfr.getStyleClass().add("text-field");

        form.add(new Label("Name:"), 0, 0); form.add(fName, 1, 0);
        form.add(new Label("Category:"), 0, 1); form.add(fCategory, 1, 1);
        form.add(new Label("Price:"), 0, 2); form.add(fPrice, 1, 2);
        form.add(new Label("Quantity:"), 0, 3); form.add(fQty, 1, 3);
        form.add(new Label("Manufacturer:"), 0, 4); form.add(fMfr, 1, 4);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Medicine"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearForm = new Button("Clear"); btnClearForm.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClearForm);

        view.getChildren().addAll(title, searchBar, table, form, btns);

        // Handlers
        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.searchMedicines(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshTable(); });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fName.setText(sel.getName()); fCategory.setValue(sel.getCategory());
                fPrice.setText(String.valueOf(sel.getPrice())); fQty.setText(String.valueOf(sel.getQuantity()));
                fMfr.setText(sel.getManufacturer());
            }
        });

        btnAdd.setOnAction(e -> {
            try {
                double price = Double.parseDouble(fPrice.getText().trim());
                int qty = Integer.parseInt(fQty.getText().trim());
                controller.addMedicine(fName.getText(), fCategory.getValue(), price, qty, fMfr.getText());
                refreshTable(); clearForm();
            } catch (NumberFormatException ex) {
                AlertUtil.showError("Error", "Price and quantity must be numbers.");
            }
        });

        btnUpdate.setOnAction(e -> {
            Medicine sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    sel.setName(fName.getText()); sel.setCategory(fCategory.getValue());
                    sel.setPrice(Double.parseDouble(fPrice.getText()));
                    sel.setQuantity(Integer.parseInt(fQty.getText())); sel.setManufacturer(fMfr.getText());
                    controller.updateMedicine(sel); refreshTable();
                } catch (NumberFormatException ex) {
                    AlertUtil.showError("Error", "Invalid number.");
                }
            } else AlertUtil.showError("Error", "Select a medicine.");
        });

        btnDelete.setOnAction(e -> {
            Medicine sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete this medicine?")) {
                controller.deleteMedicine(sel.getMedicineId()); refreshTable(); clearForm();
            }
        });

        btnClearForm.setOnAction(e -> clearForm());

        return view;
    }

    private void refreshTable() { table.setItems(FXCollections.observableArrayList(controller.getAllMedicines())); }
    private void clearForm() { fName.clear(); fCategory.setValue(null); fPrice.clear(); fQty.clear(); fMfr.clear(); }
}
