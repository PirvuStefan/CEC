package org.example.cec.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
    private final Stage stage;
    private Scene mainScene;

    public SceneController(Stage stage) {
        this.stage = stage;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void switchToMainScene() {
        if (mainScene != null) {
            stage.setScene(mainScene);
        }
    }

    public void switchToInstructionsScene() {
        Scene scene = InstructionsScene.create(this);
        stage.setScene(scene);
    }

    public void switchToWeekendDetailsScene() {
        Scene scene = WeekendDetailsScene.create(this);
        stage.setScene(scene);
    }

    public void switchToVacanteDetailsScene() {
        Scene scene = VacanteDetailsScene.create(this);
        stage.setScene(scene);
    }
}

