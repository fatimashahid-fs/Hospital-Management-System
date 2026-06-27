package com.cityhospital.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * AlertUtil is a utility class that centralizes the creation of JavaFX Alert dialogs.
 * Using this class ensures consistent styling and behavior of all alerts in the application.
 * 
 * This demonstrates code reusability and separation of concerns.
 */
public class AlertUtil {

    /**
     * Shows an Information alert dialog.
     * 
     * @param title   The title of the alert window
     * @param message The content message to display
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an Error alert dialog.
     * 
     * @param title   The title of the alert window
     * @param message The error message to display
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a Warning alert dialog.
     * 
     * @param title   The title of the alert window
     * @param message The warning message to display
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a Success (Information) alert dialog.
     */
    public static void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a Confirmation alert dialog and returns the user's choice.
     * 
     * @param title   The title of the alert window
     * @param message The confirmation message
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }
}
