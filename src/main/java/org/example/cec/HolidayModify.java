package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.holiday.Holiday;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.example.cec.ui.MainScene.daysInMonth;

public class HolidayModify {

    private File mainSheet, holidaysSheet;

    public HolidayModify(File mainSheet, File holidaysSheet) {
        this.mainSheet = mainSheet;
        this.holidaysSheet = holidaysSheet;
    }

    // holiday deletes the shift of the employee for the specific day, if the employee is on holiday, he cannot work on that day, so we need to delete the shift from the mainSheet, we also need to color the cell with the reason of the holiday ( green for concediu, pink for maternitate, blue for medical, orange for absenta and red for demisie)
    // it also deletes a WeekendShift if the employee is on holiday from monday to friday and the weekend shift on saturday will be discarded,
    // we do process the holidays first and then the weekends shifts to avoid certain conflicts
    // TODO: we do need to see if this interferes with the panama shifts, I think that the panama are not pseudo-real and algorihm based and if on the shift is marked with a X, it has actually worked there

    public File launch() {
        HolidayInitialize holidayInitialize = new HolidayInitialize(holidaysSheet);
        List<Holiday> holidays = holidayInitialize.InitialiseHolidaysList();

        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(3);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue().trim().toUpperCase() : null;
                if (name == null || name.isEmpty()) break;

                for (Holiday holiday : holidays) {
                    String normalizedMain = NormalizeName.set(name).replaceAll("[\\s\\-]+", "");
                    String normalizedHoliday = NormalizeName.set(holiday.getName()).replaceAll("[\\s\\-]+", "");

                    if (normalizedMain.equals(normalizedHoliday)) {
                        System.out.println(name.toLowerCase());
                        System.out.println("Days in month: " + daysInMonth);
                        holiday.apply(row, headerRow, daysInMonth);
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with holidays successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Holiday modification completed.");
        return mainSheet;
    }
}