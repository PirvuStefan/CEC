package org.example.cec;

import static org.example.cec.ui.MainScene.daysInMonth;
import static org.example.cec.ui.MainScene.reset;

public class VariableReset {

    public static void resetStaticVariables() {
        WeekendShift.size = 0;
        WeekendShift.pos = new int[32];
        daysInMonth = 0;
        WeekendShift.sarbatoriSize = 0;
        WeekendShift.sarbatoare = new int[32];
        reset = true;
    }
}
