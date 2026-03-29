package org.example.cec;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.cec.ui.MainScene;
import org.example.cec.ui.SceneController;


public class HelloApplication extends Application {



    @Override
    public void start(Stage stage) {

        SceneController sceneController = new SceneController(stage);
        MainScene mainScene = new MainScene(sceneController);
        sceneController.setMainScene(mainScene.getScene());
        mainScene.show(stage);
    }



}
