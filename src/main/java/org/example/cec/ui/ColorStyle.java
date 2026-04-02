package org.example.cec.ui;

import javafx.scene.control.Button;

public interface ColorStyle {

    default void styleProcessButton(Button processButton) {
        processButton.setStyle(
                "-fx-background-color: rgba(0,123,255,0.85); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;"
        );
        processButton.setOnMouseEntered(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,150,255,1); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow(gaussian, #007bff, 10, 0.5, 0, 2);"
        ));
        processButton.setOnMouseExited(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,123,255,0.85); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;"
        ));
        processButton.setOnMousePressed(e -> {
            processButton.setStyle(
                    "-fx-background-color: rgba(0,90,200,1); " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10 20 10 20;" +
                            "-fx-effect: innershadow(gaussian, #003366, 10, 0.5, 0, 2);"
            );
        });
        processButton.setOnMouseReleased(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,150,255,1); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow(gaussian, #007bff, 10, 0.5, 0, 2);"
        ));
    }
}
