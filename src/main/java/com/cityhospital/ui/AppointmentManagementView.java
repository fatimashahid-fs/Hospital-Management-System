package com.cityhospital.ui;

import com.cityhospital.controller.AppointmentController;
import com.cityhospital.model.Appointment;
import com.cityhospital.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

/**
 * AppointmentManagementView - standalone appointment management UI with CSS styling.
 */
public class AppointmentManagementView {

    private final AppointmentController controller = new AppointmentController();
    private TableView<Appointment> table;

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("APPOINTMENT MANAGEMENT");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox();
        searchBar.getStyleClass().add("search-bar");
        TextField searchField = new TextField(); searchField.getStyleClass().add("search-field");
        searchField.setPromptText("\uD83D\uDD0D  Search by ID...");
        Button btnSearch = new Button("Search"); btnSearch.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
        Button btnClear = new Button("Clear"); btnClear.getStyleClass().addAll("btn", "btn-outline", "btn-sm");
        searchBar.getChildren().addAll(searchField, btnSearch, btnClear);

        table = new TableView<>(); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        TableColumn<Appointment, String> colAId = new TableColumn<>("Appt ID");
        colAId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPId = new TableColumn<>("Patient ID");
        colPId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Appointment, String> colDId = new TableColumn<>("Doctor ID");
        colDId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, LocalDate> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colAId, colPId, colDId, colDate, colTime, colStatus);
        refreshTable();

        HBox btns = new HBox(10);
        btns.getStyleClass().add("action-bar");
        Button btnCancel = new Button("Cancel Selected"); btnCancel.getStyleClass().addAll("btn", "btn-danger");
        Button btnRefresh = new Button("Refresh"); btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btns.getChildren().addAll(btnCancel, btnRefresh);

        view.getChildren().addAll(title, searchBar, table, btns);

        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.searchAppointments(searchField.getText()))));
        btnClear.setOnAction(e -> { searchField.clear(); refreshTable(); });
        btnRefresh.setOnAction(e -> refreshTable());
        btnCancel.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && AlertUtil.showConfirmation("Cancel", "Cancel?")) { controller.cancelAppointment(sel.getAppointmentId()); refreshTable(); }
        });

        return view;
    }

    private void refreshTable() { table.setItems(FXCollections.observableArrayList(controller.getAllAppointments())); }
}
