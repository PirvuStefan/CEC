package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VacanteDetailsScene {

    public static Scene create(SceneController controller) {
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

