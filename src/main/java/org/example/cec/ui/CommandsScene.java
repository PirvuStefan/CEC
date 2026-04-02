package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CommandsScene implements ColorStyle {

	public static Scene create(SceneController controller) {
		Label title = new Label("Comenzi Aditionale");
		title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label description = new Label(
			"Alege o comanda: cauta un angajat existent sau adauga un angajat nou."
		);
		description.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

		Button searchEmployeeButton = new Button("Cauta Angajat");
		searchEmployeeButton.setPrefWidth(240);

		Button addEmployeeButton = new Button("Adauga Angajat");
		addEmployeeButton.setPrefWidth(240);

		Button backButton = new Button("Inapoi la Pagina Principala");
		backButton.setPrefWidth(240);

		CommandsScene styleHelper = new CommandsScene();
		styleHelper.styleProcessButton(searchEmployeeButton);
		styleHelper.styleProcessButton(addEmployeeButton);
		styleHelper.styleProcessButton(backButton);

		VBox layout = new VBox(20, title, description, searchEmployeeButton, addEmployeeButton, backButton);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(20));
		layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

		searchEmployeeButton.setOnAction(e -> controller.switchToSearchEmployeeScene());
		addEmployeeButton.setOnAction(e -> controller.switchToAddEmployeeScene());
		backButton.setOnAction(e -> controller.switchToMainScene());

		return new Scene(layout, 600, 400);
	}


}
