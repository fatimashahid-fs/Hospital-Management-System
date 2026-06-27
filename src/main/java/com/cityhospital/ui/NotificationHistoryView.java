package com.cityhospital.ui;

import com.cityhospital.model.NotificationRecord;
import com.cityhospital.service.FileManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class NotificationHistoryView {

    public VBox getView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(5));

        Label title = new Label("NOTIFICATION HISTORY");
        title.getStyleClass().add("page-title");

        TableView<NotificationRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        TableColumn<NotificationRecord, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<NotificationRecord, String> cType = new TableColumn<>("Type");
        cType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<NotificationRecord, String> cRecipient = new TableColumn<>("Recipient");
        cRecipient.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        TableColumn<NotificationRecord, String> cSubject = new TableColumn<>("Subject");
        cSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        TableColumn<NotificationRecord, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<NotificationRecord, String> cTime = new TableColumn<>("Sent At");
        cTime.setCellValueFactory(new PropertyValueFactory<>("sentAt"));

        table.getColumns().addAll(cId, cType, cRecipient, cSubject, cStatus, cTime);
        table.setItems(FXCollections.observableArrayList(FileManager.loadNotifications()));

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().addAll("btn", "btn-outline");
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(FileManager.loadNotifications())));

        view.getChildren().addAll(title, table, btnRefresh);
        return view;
    }
}
