package com.cityhospital.ui;

import com.cityhospital.controller.DoctorController;
import com.cityhospital.model.Doctor;
import com.cityhospital.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * DoctorManagementView - standalone doctor CRUD UI with CSS styling.
 */
public class DoctorManagementView {

    private final DoctorController controller = new DoctorController();
    private TableView<Doctor> table;
    private TextField fName, fPhone, fEmail, fSpec, fTime;

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("DOCTOR MANAGEMENT");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by name...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        table = new TableView<>(); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Doctor, String> colId = new TableColumn<>("Doctor ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Doctor, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, String> colSpec = new TableColumn<>("Specialization");
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Doctor, String> colTime = new TableColumn<>("Available");
        colTime.setCellValueFactory(new PropertyValueFactory<>("availableTime"));
        TableColumn<Doctor, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Doctor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        table.getColumns().addAll(colId, colName, colSpec, colTime, colPhone, colEmail);
        refreshTable();

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setPadding(new Insets(15, 0, 10, 0));
        fName = new TextField(); fName.setPromptText("Full Name"); fName.getStyleClass().add("text-field");
        fPhone = new TextField(); fPhone.setPromptText("03XXXXXXXXX"); fPhone.getStyleClass().add("text-field");
        fEmail = new TextField(); fEmail.setPromptText("email@example.com"); fEmail.getStyleClass().add("text-field");
        fSpec = new TextField(); fSpec.setPromptText("Specialization"); fSpec.getStyleClass().add("text-field");
        fTime = new TextField(); fTime.setPromptText("HH:mm (09:00-17:00)"); fTime.getStyleClass().add("text-field");
        form.add(new Label("Name:"), 0, 0); form.add(fName, 1, 0);
        form.add(new Label("Phone:"), 0, 1); form.add(fPhone, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(fEmail, 1, 2);
        form.add(new Label("Spec:"), 0, 3); form.add(fSpec, 1, 3);
        form.add(new Label("Time:"), 0, 4); form.add(fTime, 1, 4);

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnAdd = new Button("+ Add Doctor"); btnAdd.getStyleClass().addAll("btn", "btn-primary");
        Button btnUpdate = new Button("Update"); btnUpdate.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Delete"); btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearForm = new Button("Clear"); btnClearForm.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClearForm);

        view.getChildren().addAll(title, searchBar, table, form, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.searchDoctors(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshTable(); });
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) { fName.setText(sel.getName()); fPhone.setText(sel.getPhone()); fEmail.setText(sel.getEmail()); fSpec.setText(sel.getSpecialization()); fTime.setText(sel.getAvailableTime()); }
        });
        btnAdd.setOnAction(e -> { controller.addDoctor("", fName.getText(), fPhone.getText(), fEmail.getText(), fSpec.getText(), fTime.getText()); refreshTable(); clearForm(); });
        btnUpdate.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) { sel.setName(fName.getText()); sel.setPhone(fPhone.getText()); sel.setEmail(fEmail.getText()); sel.setSpecialization(fSpec.getText()); sel.setAvailableTime(fTime.getText()); controller.updateDoctor(sel); refreshTable(); }
            else AlertUtil.showError("Error", "Select a doctor.");
        });
        btnDelete.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Delete", "Delete?")) { controller.deleteDoctor(sel.getDoctorId()); refreshTable(); clearForm(); }
        });
        btnClearForm.setOnAction(e -> clearForm());

        return view;
    }

    private void refreshTable() { table.setItems(FXCollections.observableArrayList(controller.getAllDoctors())); }
    private void clearForm() { fName.clear(); fPhone.clear(); fEmail.clear(); fSpec.clear(); fTime.clear(); }
}
