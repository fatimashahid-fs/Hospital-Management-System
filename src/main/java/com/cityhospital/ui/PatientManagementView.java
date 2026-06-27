package com.cityhospital.ui;

import com.cityhospital.controller.PatientController;
import com.cityhospital.model.Patient;
import com.cityhospital.service.ValidationService;
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
 * PatientManagementView - standalone patient CRUD UI with CSS styling.
 */
public class PatientManagementView {

    private final PatientController controller = new PatientController();
    private TableView<Patient> table;
    private TextField fName, fPhone, fEmail, fAge, fHistory;
    private ComboBox<String> fGender;

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("PATIENT MANAGEMENT");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by name...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Patient, String> colId = new TableColumn<>("Patient ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Patient, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Patient, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Patient, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Patient, Integer> colAge = new TableColumn<>("Age");
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Patient, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        table.getColumns().addAll(colId, colName, colPhone, colEmail, colAge, colGender);
        refreshTable();

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));
        fName = new TextField(); fName.setPromptText("Full Name"); fName.getStyleClass().add("text-field");
        fPhone = new TextField(); fPhone.setPromptText("03XXXXXXXXX"); fPhone.getStyleClass().add("text-field");
        fEmail = new TextField(); fEmail.setPromptText("email@example.com"); fEmail.getStyleClass().add("text-field");
        fAge = new TextField(); fAge.setPromptText("Age"); fAge.getStyleClass().add("text-field");
        fGender = new ComboBox<>(); fGender.getItems().addAll("Male", "Female", "Other");
        fGender.setPromptText("Gender"); fGender.getStyleClass().add("combo-box");
        fHistory = new TextField(); fHistory.setPromptText("Medical History"); fHistory.getStyleClass().add("text-field");

        form.add(new Label("Name:"), 0, 0); form.add(fName, 1, 0);
        form.add(new Label("Phone:"), 0, 1); form.add(fPhone, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(fEmail, 1, 2);
        form.add(new Label("Age:"), 0, 3); form.add(fAge, 1, 3);
        form.add(new Label("Gender:"), 0, 4); form.add(fGender, 1, 4);
        form.add(new Label("History:"), 0, 5); form.add(fHistory, 1, 5);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Patient"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearForm = new Button("Clear"); btnClearForm.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClearForm);

        view.getChildren().addAll(title, searchBar, table, form, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.searchPatients(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshTable(); });
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fName.setText(sel.getName()); fPhone.setText(sel.getPhone()); fEmail.setText(sel.getEmail());
                fAge.setText(String.valueOf(sel.getAge())); fGender.setValue(sel.getGender()); fHistory.setText(sel.getMedicalHistory());
            }
        });
        btnAdd.setOnAction(e -> {
            controller.addPatient("", fName.getText(), fPhone.getText(), fEmail.getText(), fAge.getText(), fGender.getValue(), fHistory.getText());
            refreshTable(); clearForm();
        });
        btnUpdate.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                if (!ValidationService.isValidName(fName.getText())) { AlertUtil.showError("Error", "Invalid name."); return; }
                sel.setName(fName.getText()); sel.setPhone(fPhone.getText()); sel.setEmail(fEmail.getText());
                sel.setAge(ValidationService.parseAge(fAge.getText())); sel.setGender(fGender.getValue()); sel.setMedicalHistory(fHistory.getText());
                controller.updatePatient(sel); refreshTable();
            } else AlertUtil.showError("Error", "Select a patient.");
        });
        btnDelete.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete?")) { controller.deletePatient(sel.getPatientId()); refreshTable(); clearForm(); }
        });
        btnClearForm.setOnAction(e -> clearForm());

        return view;
    }

    private void refreshTable() { table.setItems(FXCollections.observableArrayList(controller.getAllPatients())); }
    private void clearForm() { fName.clear(); fPhone.clear(); fEmail.clear(); fAge.clear(); fGender.setValue(null); fHistory.clear(); }
}
