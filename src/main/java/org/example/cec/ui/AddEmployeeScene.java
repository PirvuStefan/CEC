package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.example.cec.list.add.AddEmployee;
import org.example.cec.list.Person;
import org.example.cec.list.add.config.JsonConfig;
import org.example.cec.ui.validate.ValidateAddEmployee;
import org.example.cec.ui.validate.ValidateDays;

import static org.example.cec.ui.validate.AlertUtility.showAlert;

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

        TextField fisierPrincipalField = new TextField();
        fisierPrincipalField.setPrefWidth(220);
        fisierPrincipalField.setStyle("-fx-background-radius: 8; -fx-background-color: white;");
        HBox fileInputBox = getHBox(formGrid, fisierPrincipalField);

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
        CheckBox newMonth = new CheckBox("Luna noua");
        newMonth.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        TextField zileInLunaField = new TextField();

        addRow(formGrid, 0, "Fisierul Principal:", fileInputBox);
        addRow(formGrid, 1, "Nume:", numeField);
        addRow(formGrid, 2, "Salariu:", salariuField);
        addRow(formGrid, 3, "Data angajarii:", dataAngajariiField);
        addRow(formGrid, 4, "CNP:", cnpField);
        addRow(formGrid, 5, "Functia:", functiaField);
        addRow(formGrid, 6, "Punct de lucru:", punctDeLucruField);
        addRow(formGrid, 7, "Gestiune:", gestiuneField);
        addRow(formGrid, 8, "Telefon:", telefonField);
        addRow(formGrid, 9, "CI:", ciField);
        addRow(formGrid, 10, "Domiciliu:", domiciliuField);
        addRow(formGrid, 11, "Valabilitate fisa:", valabilitateFisaField);
        addRow(formGrid, 12, "Luna noua:", newMonth);
        addRow(formGrid, 13, "Zile in luna:", zileInLunaField);

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

        saveButton.setOnAction(e -> showAlert("Datele au fost completate. Urmatorul pas este salvarea in fisier."));
        clearButton.setOnAction(e -> {
            ValidateAddEmployee.getFileds(numeField, salariuField, dataAngajariiField, cnpField, functiaField, punctDeLucruField, gestiuneField, telefonField, ciField, domiciliuField, valabilitateFisaField, newMonth);
        });
        backButton.setOnAction(e -> controller.switchToCommandsScene());

        saveButton.setOnAction(e -> {

            ValidateDays.checkAndUpdate(zileInLunaField.getText(), null);

            if(ValidateAddEmployee.check(numeField,salariuField,dataAngajariiField,cnpField,functiaField,punctDeLucruField,gestiuneField,telefonField,ciField,domiciliuField,valabilitateFisaField)){
                showAlert("Completati toate campurile necesare");
                return;
            }

            String salary;
            try {
                salary = JsonConfig.getInstance().getSalary();
            } catch (IOException ex) {
                showAlert("Eroare la incarcarea salariului default, asigurati va fisierul config.json se afla in arhiva");
                return;
            }


            String salaryStr = salariuField.getText().isEmpty() ? salary : salariuField.getText();
            String jobStr = functiaField.getText().isEmpty() ? "vanzator" : functiaField.getText();
            LocalDate hireDate = dataAngajariiField.getValue() == null ? LocalDate.now() : dataAngajariiField.getValue();

            Person.PersonBuilder builder = new Person.PersonBuilder();
            builder.setName(numeField.getText())
                    .setSalary(salaryStr)
                    .setEmploymentDate(hireDate)
                    .setCNP(cnpField.getText())
                    .setJob(jobStr)
                    .setPlaceOfWork(punctDeLucruField.getText())
                    .setGestiune(gestiuneField.getText())
                    .setPhoneNumber(telefonField.getText())
                    .setCI(ciField.getText())
                    .setDomicile(domiciliuField.getText());
            
            if (valabilitateFisaField.getValue() != null) {
                builder.setValability(valabilitateFisaField.getValue());
            }

            Person person = builder.build();

            File fileToProcess = new File(fisierPrincipalField.getText());
            AddEmployee addEmployee = new AddEmployee(person, newMonth.isSelected(), fileToProcess);
            addEmployee.start();

            showAlert("Angajatul " + person.getName() + " a fost adaugat cu succes. Verificati fisierul pentru detalii si asigurati-va ca toate datele sunt conforme.");


        });



        return new Scene(layout, 600, 400);
    }

    private static HBox getHBox(GridPane formGrid, TextField fisierPrincipalField) {
        Button browseFileButton = new Button("Browse");
        browseFileButton.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8; -fx-font-weight: bold;");
        browseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecteaza Fisierul Principal");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            File selectedFile = fileChooser.showOpenDialog(formGrid.getScene().getWindow());
            if (selectedFile != null) {
                fisierPrincipalField.setText(selectedFile.getAbsolutePath());
            }
        });
        HBox fileInputBox = new HBox(5, fisierPrincipalField, browseFileButton);
        fileInputBox.setAlignment(Pos.CENTER_LEFT);
        return fileInputBox;
    }

    private static void addRow(GridPane formGrid, int i, String s, CheckBox newMonth) {
        Label label = new Label(s);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        formGrid.add(label, 0, i);
        formGrid.add(newMonth, 1, i);
    }

    private static void addRow(GridPane grid, int rowIndex, String labelText, HBox input) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        grid.add(label, 0, rowIndex);
        grid.add(input, 1, rowIndex);
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
