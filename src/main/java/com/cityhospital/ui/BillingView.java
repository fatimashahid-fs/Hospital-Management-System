package com.cityhospital.ui;

import com.cityhospital.controller.BillingController;
import com.cityhospital.model.Appointment;
import com.cityhospital.model.Bill;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.util.List;

/**
 * BillingView — Invoice generation and payment management UI.
 */
public class BillingView {

    private final BillingController controller = new BillingController();
    private TableView<Bill> billTable;
    private TableView<Appointment> appointmentTable;

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("BILLING & INVOICES");
        title.getStyleClass().add("page-title");

        // ===== APPOINTMENT SELECTION =====
        Label sectionA = new Label("Step 1: Select an Appointment");
        sectionA.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 10 0 5 0;");

        appointmentTable = new TableView<>();
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        appointmentTable.getStyleClass().add("table-view");
        appointmentTable.setPrefHeight(150);

        TableColumn<Appointment, String> colAId = new TableColumn<>("Appt ID");
        colAId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient ID");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        appointmentTable.getColumns().addAll(colAId, colPId, colDate, colStatus);
        appointmentTable.setItems(FXCollections.observableArrayList(
            FileManager.loadAppointments().stream()
                .filter(a -> Appointment.STATUS_COMPLETED.equals(a.getStatus()) || Appointment.STATUS_SCHEDULED.equals(a.getStatus()))
                .toList()
        ));

        // ===== BILL FORM =====
        Label sectionB = new Label("Step 2: Enter Charges");
        sectionB.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 10 0 5 0;");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");

        Label lblAppt = new Label("Selected Appt:");
        Label lblApptVal = new Label("(none)");
        lblApptVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B0000;");

        TextField fDoctorFee = new TextField(); fDoctorFee.setPromptText("e.g. 1500"); fDoctorFee.getStyleClass().add("text-field");
        TextField fMedCharges = new TextField(); fMedCharges.setPromptText("e.g. 500"); fMedCharges.getStyleClass().add("text-field");
        TextField fLabCharges = new TextField(); fLabCharges.setPromptText("e.g. 0"); fLabCharges.getStyleClass().add("text-field");

        ComboBox<String> cmbPayment = new ComboBox<>();
        cmbPayment.getItems().addAll(Bill.STATUS_PAID, Bill.STATUS_UNPAID, Bill.STATUS_PENDING);
        cmbPayment.setPromptText("Payment Status");
        cmbPayment.getStyleClass().add("combo-box");

        form.add(new Label("Doctor Fee (Rs):"), 0, 0); form.add(fDoctorFee, 1, 0);
        form.add(new Label("Medicine Charges (Rs):"), 0, 1); form.add(fMedCharges, 1, 1);
        form.add(new Label("Lab Charges (Rs):"), 0, 2); form.add(fLabCharges, 1, 2);
        form.add(new Label("Payment Status:"), 0, 3); form.add(cmbPayment, 1, 3);
        form.add(lblAppt, 0, 4); form.add(lblApptVal, 1, 4);

        Button btnCreate = new Button("Generate Invoice");
        btnCreate.getStyleClass().addAll("btn", "btn-primary");

        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) lblApptVal.setText(sel.getAppointmentId() + " | Patient: " + sel.getPatientId());
        });

        btnCreate.setOnAction(e -> {
            Appointment sel = appointmentTable.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertUtil.showError("Error", "Select an appointment."); return; }
            double docFee, medCh, labCh;
            try {
                docFee = Double.parseDouble(fDoctorFee.getText().trim());
                medCh = Double.parseDouble(fMedCharges.getText().trim());
                labCh = Double.parseDouble(fLabCharges.getText().trim());
            } catch (NumberFormatException ex) {
                AlertUtil.showError("Error", "Enter valid numeric charges."); return;
            }
            String pName = FileManager.loadPatients().stream()
                    .filter(p -> p.getPatientId().equals(sel.getPatientId()))
                    .findFirst().map(p -> p.getName()).orElse("Unknown");
            controller.createBill(sel.getAppointmentId(), sel.getPatientId(), pName,
                                  docFee, medCh, labCh, cmbPayment.getValue());
            refreshBillTable();
            fDoctorFee.clear(); fMedCharges.clear(); fLabCharges.clear(); cmbPayment.setValue(null);
        });

        VBox formCard = new VBox(15, form, btnCreate);
        formCard.getStyleClass().add("card");

        // ===== BILL LIST =====
        Label sectionC = new Label("All Invoices");
        sectionC.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 10 0 5 0;");

        // Search
        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by ID or patient...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        billTable = new TableView<>();
        billTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        billTable.getStyleClass().add("table-view");

        TableColumn<Bill, String> cId = new TableColumn<>("Bill ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        TableColumn<Bill, String> cAppt = new TableColumn<>("Appt ID");
        cAppt.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Bill, String> cPat = new TableColumn<>("Patient");
        cPat.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Bill, Double> cTotal = new TableColumn<>("Total (Rs)");
        cTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<Bill, String> cStatus = new TableColumn<>("Payment");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        billTable.getColumns().addAll(cId, cAppt, cPat, cTotal, cStatus);
        refreshBillTable();

        HBox actionBtns = new HBox(10);
        actionBtns.getStyleClass().add("action-bar");
        Button btnMarkPaid = new Button("Mark as Paid"); btnMarkPaid.getStyleClass().addAll("btn", "btn-success");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        actionBtns.getChildren().addAll(btnMarkPaid, btnRefresh);

        btnMarkPaid.setOnAction(e -> {
            Bill sel = billTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.updatePaymentStatus(sel.getBillId(), Bill.STATUS_PAID);
                refreshBillTable();
            }
        });
        btnRefresh.setOnAction(e -> refreshBillTable());
        btnSearch.setOnAction(e -> billTable.setItems(FXCollections.observableArrayList(controller.searchBills(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshBillTable(); });

        view.getChildren().addAll(title, sectionA, appointmentTable, sectionB, formCard, sectionC, searchBar, billTable, actionBtns);
        return view;
    }

    private void refreshBillTable() {
        billTable.setItems(FXCollections.observableArrayList(controller.getAllBills()));
    }
}
