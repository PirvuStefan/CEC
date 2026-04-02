package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddEmployeeScene implements ColorStyle {

    public static Scene create(SceneController controller) {
        Label title = new Label("Adauga Angajat");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label description = new Label("Completeaza datele angajatului nou.");
        description.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(12);
        formGrid.setAlignment(Pos.CENTER);

        TextField numeField = new TextField();
        TextField salariuField = new TextField();
        DatePicker dataAngajariiField = new DatePicker();
        TextField cnpField = new TextField();
        TextField functiaField = new TextField();
        TextField punctDeLucruField = new TextField();
        TextField gestiuneField = new TextField();
        TextField telefonField = new TextField();
        TextField ciField = new TextField();
        TextField domiciliuField = new TextField();
        DatePicker valabilitateFisaField = new DatePicker();

        addRow(formGrid, 0, "Nume:", numeField);
        addRow(formGrid, 1, "Salariu:", salariuField);
        addRow(formGrid, 2, "Data angajarii:", dataAngajariiField);
        addRow(formGrid, 3, "CNP:", cnpField);
        addRow(formGrid, 4, "Functia:", functiaField);
        addRow(formGrid, 5, "Punct de lucru:", punctDeLucruField);
        addRow(formGrid, 6, "Gestiune:", gestiuneField);
        addRow(formGrid, 7, "Telefon:", telefonField);
        addRow(formGrid, 8, "CI:", ciField);
        addRow(formGrid, 9, "Domiciliu:", domiciliuField);
        addRow(formGrid, 10, "Valabilitate fisa:", valabilitateFisaField);

        ScrollPane scrollPane = new ScrollPane(formGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button saveButton = new Button("Salveaza");
        saveButton.setPrefWidth(140);

        Button backButton = new Button("Inapoi la Comenzi");
        backButton.setPrefWidth(180);

        Button clearButton = new Button("Goleste");
        clearButton.setPrefWidth(140);

        AddEmployeeScene styleHelper = new AddEmployeeScene();
        styleHelper.styleProcessButton(saveButton);
        styleHelper.styleProcessButton(clearButton);
        styleHelper.styleProcessButton(backButton);

        HBox actions = new HBox(12, saveButton, clearButton, backButton);
        actions.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, title, description, scrollPane, actions);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        saveButton.setOnAction(e -> MainScene.showAlert("Datele au fost completate. Urmatorul pas este salvarea in fisier."));
        clearButton.setOnAction(e -> {
            numeField.clear();
            salariuField.clear();
            dataAngajariiField.setValue(null);
            cnpField.clear();
            functiaField.clear();
            punctDeLucruField.clear();
            gestiuneField.clear();
            telefonField.clear();
            ciField.clear();
            domiciliuField.clear();
            valabilitateFisaField.setValue(null);
        });
        backButton.setOnAction(e -> controller.switchToCommandsScene());

        return new Scene(layout, 600, 400);
    }

    private static void addRow(GridPane grid, int rowIndex, String labelText, TextField input) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        input.setPrefWidth(260);
        input.setStyle("-fx-background-radius: 8; -fx-background-color: white;");
        grid.add(label, 0, rowIndex);
        grid.add(input, 1, rowIndex);
    }

    private static void addRow(GridPane grid, int rowIndex, String labelText, DatePicker input) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        input.setPrefWidth(260);
        input.setStyle("-fx-background-radius: 8; -fx-background-color: white;");
        grid.add(label, 0, rowIndex);
        grid.add(input, 1, rowIndex);
    }
}

