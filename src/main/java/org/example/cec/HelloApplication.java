package org.example.cec;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HelloApplication extends Application {

    private File mainSheet;
    private File weekendSheet;
    private File holidaysSheet;
    static int daysInMonth;
    private int firstDayOfWeekend;
    // we need to know how many days are in a month and what is the first day of the first weekend ( like to know how to count and take in consideration the weekends when we process the data)

    @Override
    public void start(Stage stage) {
        Path outputDir = Path.of("arhiva");
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
            createFileSelector("Fisierul Vacante", file -> holidaysSheet = file)
        );
        fileSelectors.getChildren().add(daysBox);

        Button processButton = new Button("Proceseaza Datele");
        processButton.setPrefWidth(200);
        styleProcessButton(processButton);
        processButton.setOnAction(e -> {
            if( daysInMonth == 0 ){
                showAlert("Te rog introdu numarul de zile din luna!");
                return;
            }
            if (weekendSheet == null && holidaysSheet == null) {
                showAlert("Te rog selecteaza toate fisierele necesare!");
                return;
            }
            if(mainSheet == null){
                showAlert("Fisierul principal nu a fost selectat!");
                return;
            }
            if(weekendSheet != null && holidaysSheet == null){
                File modifiedSheet = WeekendModify.Launch(mainSheet, weekendSheet);
                if (modifiedSheet != null) {
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de weekend!");
                resetStaticVariables();
                clearFileSelections(fileSelectors);
                return;

            }
            if(weekendSheet == null && holidaysSheet != null){
                File modifiedSheet = HolidayModify.Launch(mainSheet, holidaysSheet);
                if (modifiedSheet != null) {
                    try {
                        Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert("Failed to copy the modified file to the output directory.");
                    }
                }
                showAlert("Fisierul principal a fost modificat cu succes folosind fisierul de concedii!");
                resetStaticVariables();
                clearFileSelections(fileSelectors);
                return;
            }

            File modifiedSheet = HolidayModify.Launch(mainSheet, holidaysSheet);
            WeekendModify.Launch(modifiedSheet, weekendSheet);
            if (modifiedSheet != null) {
                try {
                    Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Failed to copy the modified file to the output directory.");
                }
            }

            showAlert("Fisierul principal a fost modificat cu succes folosind ambele fisiere!");
            resetStaticVariables();
            clearFileSelections(fileSelectors);



        });


        Button deleteButton = new Button("Sterge Datele");
        deleteButton.setPrefWidth(200);
        styleProcessButton(deleteButton);
        deleteButton.setOnAction(e -> {
            stage.setScene(createDeleteDataScene(stage, stage.getScene()));
        });


        Button instructionsButton = new Button("Cum folosim aplicatia?");
        instructionsButton.setPrefWidth(200);
        instructionsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        VBox root = new VBox(30, title, fileSelectors, processButton, deleteButton, instructionsButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene mainScene = new Scene(root, 600, 500);
        stage.setScene(mainScene);
        stage.setTitle("Central Excel Controller");
        stage.setMinWidth(500);
        stage.setMinHeight(350);
        stage.show();

        instructionsButton.setOnAction(e -> stage.setScene(createInstructionsScene(stage, mainScene))); // Redirect to instructions page
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
    }

    private Scene createInstructionsScene(Stage stage, Scene mainScene) {
        Label title = new Label("Ghid De Utilizare  \u2714");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label instructions = new Label(
            """
            1. Selecteaza fisierele necesare apasand pe butonul cauta .
            2. Fii sigur ca fisierele sunt corect formatate .
            3. Apasa 'Proceseaza Datele' .
            4. Fisierul modificat se poate gasi in folderul 'arhiva' .
            5. Nu uita sa faci o copie de siguranta a fisierelor originale inainte de procesare.
            6. Toate sheet-urile trebuie sa fie pe pozitia 0 ( primul sheet din excel ) .
            """
        );
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Inapoi la Pagina Principala");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        Button weekendDetailsButton = new Button("Detalii Weekend");
        weekendDetailsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        Button vacanteDetailsButton = new Button("Detalii Vacante");
        vacanteDetailsButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        VBox layout = new VBox(20, title, instructions, weekendDetailsButton, vacanteDetailsButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        Scene instructionsScene = new Scene(layout, 600, 400);

        backButton.setOnAction(e -> stage.setScene(mainScene));
        weekendDetailsButton.setOnAction(e -> stage.setScene(createWeekendDetailsScene(stage, instructionsScene)));
        vacanteDetailsButton.setOnAction(e -> stage.setScene(createVacanteDetailsScene(stage, instructionsScene)));

        return instructionsScene;
    }



    private Scene createWeekendDetailsScene(Stage stage, Scene instructionsScene) {
        Label title = new Label("Detalii Weekend");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label details = new Label(
                """
               Functionalitatea Weekend:
               - Permite modificarea fisierului principal pe baza programului de weekend.
               - Aloca automat turele de weekend angajatilor, tinand cont de istoricul lor si de numarul de ture necesare.
               - Zilele de sambata si duminica sunt evidentiate si gestionate separat.
               - Orice modificare este salvata in fisierul principal si arhivata in folderul 'arhiva'.
               - Pentru o functionare corecta, asigurati-va ca fisierul de weekend este bine formatat si contine toate informatiile necesare.
               - Fiecare angajat trebuie sa aiba un numar specific de ture si sa fie marcat daca a lucrat sambata in luna precedenta.
               - In cazul in care luna incepe cu o duminica, se va tine cont daca angajatul a lucrat sambata in luna precedenta pentru a aloca turele corect.
               - Pentru o gestionare eficienta, este esential ca numarul de ture alocate fiecarui angajat sa nu fie mai mare decat numarul de weekend-uri din luna respectiva.
               """
        );
        details.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Înapoi la Ghid");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(instructionsScene));

        VBox layout = new VBox(20, title, details, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(layout, 600, 400);
    }

    private Scene createVacanteDetailsScene(Stage stage, Scene instructionsScene) {
        Label title = new Label("Detalii Vacanțe");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label details = new Label(
                """
               Functionalitatea Vacante:
                - Permite modificarea fisierului principal pe baza fisierului de vacante/concedii.
                - Zilele de concediu, medical, maternitate, absenta sau demisie sunt marcate distinct.
                - Culorile specifice sunt folosite pentru a evidentia diferitele tipuri de vacante:
                  - Concediu: Verde ( co )
                  - Maternitate: Roz ( m )
                  - Medical: Albastru deschis ( cm )
                  - Absenta: Portocaliu ( abs )
                  - Demisie: Rosu ( dem )
                - Zilele de weekend care coincid cu vacanta sunt eliberate automat.
                - In tabelul excel, in coloana "periodata" se introduce perioada vacantei in formatul "zz*zz" (ex: 10*23).
                - In coloana "tip concediu" se introduce tipul vacantei folosind abrevierile specificate mai sus.
                - Daca o persoana are mai multe perioade de vacanta, se adauga cate un rand separat pentru fiecare perioada.
                - Toate modificarile sunt salvate si arhivate in folderul 'arhiva'.
                """
        );
        details.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10;");

        Button backButton = new Button("Înapoi la Ghid");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(instructionsScene));

        VBox layout = new VBox(20, title, details, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(layout, 600, 400);
    }

    private HBox createFileSelector(String labelText, FileConsumer fileSetter) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField filePath = new TextField();
        filePath.setEditable(true); // to enable text editing in the filepath
        filePath.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

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
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(filePath, Priority.ALWAYS);
        return box;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FunctionalInterface
    interface FileConsumer {
        void setFile(File file);
    }

    public static void main(String[] args) {
        launch(args);
    }






    private void styleProcessButton(Button processButton) {
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
//           javafx.scene.media.AudioClip clickSound = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/click.mp3").toExternalForm());
//           clickSound.play();
       });//Make sure to place your sound file (e.g., click.mp3) in the resources/sounds directory of your project.
        processButton.setOnMouseReleased(e -> processButton.setStyle(
                "-fx-background-color: rgba(0,150,255,1); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow(gaussian, #007bff, 10, 0.5, 0, 2);"
        ));
    }



    private Scene createDeleteDataScene(Stage stage, Scene mainScene) {
        Label title = new Label("Sterge Date Programate");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox fileSelectors = new VBox(20);
        fileSelectors.setPadding(new Insets(20));
        fileSelectors.setAlignment(Pos.CENTER);

        // Excel file selector
        Label excelLabel = new Label("Selecteaza Fisierul Excel Principal:");
        excelLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField excelFilePath = new TextField();
        excelFilePath.setPromptText("Calea catre fisierul Excel...");
        excelFilePath.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

        Button browseButton = new Button("Cauta");
        browseButton.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8; -fx-font-weight: bold;");

        final File[] selectedFile = {null};
        browseButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                excelFilePath.setText(file.getAbsolutePath());
                selectedFile[0] = file;
            }
        });

        HBox excelBox = new HBox(10, excelLabel, excelFilePath, browseButton);
        excelBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(excelFilePath, Priority.ALWAYS);

        // Shop center input
        Label shopLabel = new Label("Centru Comercial:");
        shopLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField shopInput = new TextField();
        shopInput.setPromptText("Numele centrului comercial...");
        shopInput.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

        HBox shopBox = new HBox(10, shopLabel, shopInput);
        shopBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(shopInput, Priority.ALWAYS);

        fileSelectors.getChildren().addAll(excelBox, shopBox);

        // Process button
        Button processDeleteButton = new Button("Proceseaza Stergerea");
        processDeleteButton.setPrefWidth(200);
        styleProcessButton(processDeleteButton);
        processDeleteButton.setOnAction(e -> {
            if (selectedFile[0] == null) {
                showAlert("Te rog selecteaza un fisier Excel!");
                return;
            }
            if (shopInput.getText().trim().isEmpty()) {
                showAlert("Te rog introdu numele centrului comercial!");
                return;
            }

            // TODO: Implement deletion logic here
            // Call a method to delete data for the specified shop center
            boolean success = deleteShopCenterData(selectedFile[0], shopInput.getText().trim());

            if (success) {
                showAlert("Datele pentru " + shopInput.getText().trim() + " au fost sterse cu succes!");
                stage.setScene(mainScene);
            } else {
                showAlert("Eroare la stergerea datelor!");
            }
        });

        // Back button
        Button backButton = new Button("Inapoi la Pagina Principala");
        backButton.setPrefWidth(200);
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        VBox root = new VBox(30, title, fileSelectors, processDeleteButton, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(root, 600, 500);
    }

    private boolean deleteShopCenterData(File excelFile, String shopCenter) {
        // TODO: Implement the logic to delete data for the specified shop center
        // This is a placeholder - you'll need to implement the actual deletion logic
        // based on your Excel structure and requirements
        try {
            // Example: Open workbook, find rows with matching shop center, delete them
            // Return true if successful, false otherwise
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void resetStaticVariables(){
        WeekendShift.size = 0;
        WeekendShift.pos = new int[32];
        daysInMonth = 0;
        WeekendShift.sarbatoriSize = 0;
        WeekendShift.sarbatoare = new int[32];
    }






}
