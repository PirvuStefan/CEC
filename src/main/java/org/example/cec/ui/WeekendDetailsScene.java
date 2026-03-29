package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WeekendDetailsScene {

    public static Scene create(SceneController controller) {
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

        Button backButton = new Button("Inapoi la Ghid");
        backButton.setStyle("-fx-background-color: rgba(0,123,255,0.85); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> controller.switchToInstructionsScene());

        VBox layout = new VBox(20, title, details, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

        return new Scene(layout, 600, 400);
    }
}

