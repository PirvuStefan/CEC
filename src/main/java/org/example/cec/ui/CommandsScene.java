package org.example.cec.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.cec.list.update.CountUpdate;
import org.example.cec.list.update.NewYearMigrate;

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

		Button migrateYearButton = new Button("Migreaza Anul");
		migrateYearButton.setPrefWidth(240);

		Button updateListButton = new Button("Updateaza Lista");
		updateListButton.setPrefWidth(240);

		Button backButton = new Button("Inapoi la Pagina Principala");
		backButton.setPrefWidth(240);

		CommandsScene styleHelper = new CommandsScene();
		styleHelper.styleProcessButton(searchEmployeeButton);
		styleHelper.styleProcessButton(addEmployeeButton);
		styleHelper.styleProcessButton(migrateYearButton);
		styleHelper.styleProcessButton(updateListButton);
		styleHelper.styleProcessButton(backButton);

		VBox layout = new VBox(20, title, description, searchEmployeeButton, addEmployeeButton, migrateYearButton, updateListButton, backButton);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(20));
		layout.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(0,100,200,0.85), rgba(0,180,255,0.85));");

		searchEmployeeButton.setOnAction(e -> controller.switchToSearchEmployeeScene());
		addEmployeeButton.setOnAction(e -> controller.switchToAddEmployeeScene());
		migrateYearButton.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmare");
			alert.setHeaderText(null);
			alert.setContentText("Sunteti sigur ca vreti sa marcati inceputul unul an nou in lista?");

			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					NewYearMigrate newYearMigrate = new NewYearMigrate();
					newYearMigrate.update();
				}
			});
		});
		
		updateListButton.setOnAction(e -> CountUpdate.start());
		
		backButton.setOnAction(e -> controller.switchToMainScene());

		return new Scene(layout, 600, 400);
	}


}
