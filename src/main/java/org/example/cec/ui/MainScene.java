package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.cec.HolidayModify;
import org.example.cec.PanamaModify;
import org.example.cec.VariablesReset;
import org.example.cec.WeekendModify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MainScene implements VariablesReset {

    private final Scene scene;
    private final SceneController sceneController;

    private File mainSheet;
    private File weekendSheet;
    private File holidaysSheet;
    private File panamaSheet;
    public static int daysInMonth;
    public static boolean reset;

    private final Path outputDir = Path.of("arhiva");

    public MainScene(SceneController sceneController) {
        this.sceneController = sceneController;

        // Ensure output directory exists
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label title = new Label("Excel Sheet Selector");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox fileSelectors = new VBox(20);
        fileSelectors.setPadding(new Insets(20));
        fileSelectors.setAlignment(Pos.CENTER);

        // Add input for "How many days are in the month?"
        Label daysLabel = new Label("Cate zile sunt in luna asta?");
        daysLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        TextField daysInput = new TextField();
        daysInput.setPromptText("numarul de zile (e.g. 30)");
        daysInput.setStyle("-fx-background-radius: 8; -fx-background-color: white;");
        daysInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (reset) {
                reset = false;
                return;
            }
            try {
                int val = Integer.parseInt(newVal.trim());
                if (val >= 1 && val <= 31) {
                    daysInMonth = val;
                }
            } catch (NumberFormatException ignored) {
                showAlert("Te rog introdu un numar valid intre 1 si 31 pentru zilele din luna!");
                daysInput.clear();
            }
        });
        HBox daysBox = new HBox(10, daysLabel, daysInput);
        daysBox.setAlignment(Pos.CENTER);

        fileSelectors.getChildren().addAll(
            createFileSelector("Fisierul Principal", file -> mainSheet = file),
            createFileSelector("Fisierul Weekend", file -> weekendSheet = file),
            createFileSelector("Fisierul Munca Inegala", file -> panamaSheet = file),
            createFileSelector("Fisierul Vacante", file -> holidaysSheet = file)
        );
        fileSelectors.getChildren().add(daysBox);

        Button processButton = new Button("Proceseaza Datele");
        processButton.setPrefWidth(200);
        styleProcessButton(processButton);
        processButton.setOnAction(e -> {
            if (daysInMonth == 0) {
                showAlert("Te rog introdu numarul de zile din luna!");
                return;
            }
            if (weekendSheet == null && holidaysSheet == null && panamaSheet == null) {
                showAlert("Te rog selecteaza toate fisierele necesare!");
                return;
            }
            if (mainSheet == null) {
                showAlert("Fisierul principal nu a fost selectat!");
                return;
            }

            if (holidaysSheet != null) {
                HolidayModify holidayModify = new HolidayModify(mainSheet, holidaysSheet);
                File modifiedSheet = holidayModify.launch();
                if (modifiedSheet != null) {
                    mainSheet = modifiedSheet;
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
            }

            if (weekendSheet != null) {
                WeekendModify weekendModify = new WeekendModify(mainSheet, weekendSheet);
                File modifiedSheet = weekendModify.launch(mainSheet, weekendSheet);
                if (modifiedSheet != null) {
                    mainSheet = modifiedSheet;
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
            }

            if (panamaSheet != null) {
                File modifiedSheet = PanamaModify.launch(mainSheet, panamaSheet);
                if (modifiedSheet != null) {
                    mainSheet = modifiedSheet;
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
            }

            showAlert("Fisierul principal a fost modificat cu succes folosind ambele fisiere!");
            resetStaticVariables();
            clearFileSelections(fileSelectors);
        });

        Button instructionsButton = new Button("Cum folosim aplicatia?");
        instructionsButton.setPrefWidth(200);
        instructionsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        VBox root = new VBox(30, title, fileSelectors, processButton, instructionsButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        this.scene = new Scene(root, 600, 500);

        // Register scene with controller so instructions can switch back
        sceneController.setMainScene(this.scene);

        instructionsButton.setOnAction(e -> sceneController.switchToInstructionsScene());
    }

    public Scene getScene() {
        return scene;
    }

    public void show(Stage stage) {
        stage.setScene(this.scene);
        stage.setTitle("Central Excel Controller");
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();
    }

    private void clearFileSelections(VBox fileSelectors) {
        for (javafx.scene.Node node : fileSelectors.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof TextField) {
                        ((TextField) child).clear();
                    }
                }
            }
            if (node instanceof TextField) {
                ((TextField) node).clear();
            }
        }
        mainSheet = null;
        weekendSheet = null;
        holidaysSheet = null;
        panamaSheet = null;
    } // this method might be converted in an interface in the future if we want to reset the file selections from other scenes as well
    // TODO : convert this to an interface if necessary

    private HBox createFileSelector(String labelText, FileConsumer fileSetter) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField filePath = new TextField();
        filePath.setEditable(true);
        filePath.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

        HBox box = getHBox(fileSetter, filePath, label);
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(filePath, Priority.ALWAYS);
        return box;
    }

    private static HBox getHBox(FileConsumer fileSetter, TextField filePath, Label label) {
        Button browseButton = new Button("Browse");
        browseButton.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8; -fx-font-weight: bold;");
        browseButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            File selectedFile = fc.showOpenDialog(null);
            if (selectedFile != null) {
                filePath.setText(selectedFile.getAbsolutePath());
                fileSetter.setFile(selectedFile);
            }
        });

        HBox box = new HBox(10, label, filePath, browseButton);
        return box;
    }

    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    interface FileConsumer {
        void setFile(File file);
    }

    static void styleProcessButton(Button processButton) {
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

