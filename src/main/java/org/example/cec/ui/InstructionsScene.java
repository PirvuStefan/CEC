package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InstructionsScene {

    public static Scene create(SceneController controller) {
        Label title = new Label("Ghid De Utilizare  \u2714");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label instructions = getLabel();

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

        backButton.setOnAction(e -> controller.switchToMainScene());
        weekendDetailsButton.setOnAction(e -> controller.switchToWeekendDetailsScene());
        vacanteDetailsButton.setOnAction(e -> controller.switchToVacanteDetailsScene());

        return instructionsScene;
    }

    private static Label getLabel() {
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
        return instructions;
    }
}

