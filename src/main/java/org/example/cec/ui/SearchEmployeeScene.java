package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.cec.list.SearchEmployee;

public class SearchEmployeeScene {

    private final Scene scene;
    private final SceneController sceneController;
    private final TextArea resultsArea;

    public SearchEmployeeScene(SceneController sceneController) {
        this.sceneController = sceneController;

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #2c3e50;");

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
        searchInput.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        searchButton.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 5;");

        searchBox.getChildren().addAll(searchLabel, searchInput, searchButton);

        // Results Area
        Label resultsLabel = new Label("Search Results:");
        resultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        this.resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setPrefHeight(300);
        resultsArea.setStyle("-fx-control-inner-background: #ecf0f1; -fx-text-fill: black;");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 5;");
        backButton.setOnAction(e -> sceneController.switchToMainScene());

        root.getChildren().addAll(title, searchBox, resultsLabel, resultsArea, backButton);

        this.scene = new Scene(root, 800, 600);




        searchButton.setOnAction(e -> {
            String text = searchInput.getText();
            SearchEmployee Search = new SearchEmployee(text);
            int key = Search.getKey();
            if(key == -1) {
                resultsArea.setText("Niciun angajat cu numele \"" + text + "\" nu a fost gasit.");
            } else {
                resultsArea.setText(Integer.toString(key));
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
