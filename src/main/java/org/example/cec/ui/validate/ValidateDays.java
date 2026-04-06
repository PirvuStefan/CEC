package org.example.cec.ui.validate;

import javafx.scene.control.TextField;
import org.example.cec.ui.MainScene;

import static org.example.cec.ui.validate.AlertUtility.showAlert;

public class ValidateDays {

    public static void checkAndUpdate(String input, TextField fieldToClear) {
        try {
            int val = Integer.parseInt(input.trim());
            if (val >= 1 && val <= 31) {
                MainScene.daysInMonth = val;
            }
        } catch (NumberFormatException ignored) {
            showAlert("Te rog introdu un numar valid intre 1 si 31 pentru zilele din luna!");
            if (fieldToClear != null) {
                fieldToClear.clear();
            }
        }
    }
}

