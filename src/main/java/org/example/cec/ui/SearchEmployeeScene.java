package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.cec.list.Person;
import org.example.cec.list.SearchEmployee;

public class SearchEmployeeScene implements ColorStyle {

    private final Scene scene;
    private final SceneController sceneController;
    private final TextArea resultsArea;

    public SearchEmployeeScene(SceneController sceneController) {
        this.sceneController = sceneController;

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        // Title
        Label title = new Label("Search Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Search Input Box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Employee Name:");
        searchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField searchInput = new TextField();
        searchInput.setPromptText("Enter employee name...");
        searchInput.setPrefWidth(300);
        searchInput.setStyle("-fx-background-radius: 8; -fx-background-color: white; -fx-padding: 8;");

        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        styleProcessButton(searchButton);

        searchBox.getChildren().addAll(searchLabel, searchInput, searchButton);

        // Results Area
        Label resultsLabel = new Label("Search Results:");
        resultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        this.resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setPrefHeight(300);
        resultsArea.setStyle("-fx-background-radius: 8; -fx-control-inner-background: white; -fx-text-fill: black;");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        styleProcessButton(backButton);
        backButton.setOnAction(e -> sceneController.switchToCommandsScene());

        root.getChildren().addAll(title, searchBox, resultsLabel, resultsArea, backButton);

        this.scene = new Scene(root, 800, 600);




        searchButton.setOnAction(e -> {
            String text = searchInput.getText();
            SearchEmployee search = new SearchEmployee(text);
            int key = search.getKey();
            if (key == -1) {
                resultsArea.setText("Niciun angajat cu numele \"" + text + "\" nu a fost gasit.");
            } else {
                Person person = search.getPerson();
                if (person != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Angajat gasit (key=").append(key).append("):\n\n");
                    sb.append("Nume: ").append(person.getName()).append("\n");
                    sb.append("Data angajarii: ").append(search.getEmploymentDateStr()).append("\n");
                    sb.append("Job: ").append(person.getJob()).append("\n");
                    sb.append("Salariu: ").append(person.getSalary()).append("\n");
                    sb.append("CNP: ").append(person.getCNP()).append("\n");
                    sb.append("Telefon: ").append(person.getPhoneNumber()).append("\n");
                    sb.append("CI: ").append(person.getCI()).append("\n");
                    sb.append("Valabilitate CI: ").append(search.getValabilityStr()).append("\n");
                    sb.append("Loc de munca: ").append(person.getPlaceOfWork()).append("\n");
                    sb.append("Gestiune: ").append(person.getGestiune()).append("\n");
                    sb.append("Domiciliu: ").append(person.getDomicile());
                    resultsArea.setText(sb.toString());
                } else {
                    resultsArea.setText("Angajat gasit (key=" + key + ") dar datele nu au putut fi citite.");
                }
            }
        });




    }



    public static Scene create(SceneController sceneController) {
        SearchEmployeeScene scene = new SearchEmployeeScene(sceneController);
        return scene.scene;
    }

    public Scene getScene() {
        return scene;
    }
}
