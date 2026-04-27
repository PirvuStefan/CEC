package org.example.cec.ui;

import org.example.cec.HolidayModify;
import org.example.cec.PanamaModify;
import org.example.cec.WeekendModify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.example.cec.ui.validate.AlertUtility.showAlert;

public class SheetModificationFacade {

    private final Path outputDir;

    public SheetModificationFacade(Path outputDir) {
        this.outputDir = outputDir;
    }

    public File processSheets(File mainSheet, File holidaysSheet, File weekendSheet, File panamaSheet) {
        if (holidaysSheet != null) {
            mainSheet = processHoliday(mainSheet, holidaysSheet);
        }

        if (weekendSheet != null) {
            mainSheet = processWeekend(mainSheet, weekendSheet);
        }

        if (panamaSheet != null) {
            mainSheet = processPanama(mainSheet, panamaSheet);
        }

        return mainSheet;
    }

    private File processHoliday(File mainSheet, File holidaysSheet) {
        HolidayModify holidayModify = new HolidayModify(mainSheet, holidaysSheet);
        File modifiedSheet = holidayModify.launch();
        return copyToOutput(mainSheet, modifiedSheet);
    }

    private File processWeekend(File mainSheet, File weekendSheet) {
        WeekendModify weekendModify = new WeekendModify(mainSheet, weekendSheet);
        File modifiedSheet = weekendModify.launch();
        return copyToOutput(mainSheet, modifiedSheet);
    }

    private File processPanama(File mainSheet, File panamaSheet) {
        PanamaModify panamaModify = new PanamaModify(mainSheet, panamaSheet);
        File modifiedSheet = panamaModify.launch();
        return copyToOutput(mainSheet, modifiedSheet);
    }

    private File copyToOutput(File originalSheet, File modifiedSheet) {
        if (modifiedSheet != null) {
            try {
                Files.copy(modifiedSheet.toPath(), outputDir.resolve(modifiedSheet.getName()), StandardCopyOption.REPLACE_EXISTING);
                return modifiedSheet;
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Failed to copy the modified file to the output directory.");
            }
        }
        return originalSheet;
    }
}

