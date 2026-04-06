package org.example.cec.ui.validate;

import javafx.scene.control.Alert;

public class AlertUtility {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

