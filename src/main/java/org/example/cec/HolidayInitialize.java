package org.example.cec;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HolidayInitialize {

    private File holidaysSheet;

    public HolidayInitialize(File holidaysSheet) {
        this.holidaysSheet = holidaysSheet;
    }

    protected List<Holiday> InitialiseHolidaysList() {
        List<Holiday> holidays = new ArrayList<>();
        String filePath = this.holidaysSheet.getAbsolutePath();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getSheetName() );

            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;

                String name = (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) ? row.getCell(0).getStringCellValue() : null;
                if( name == null || name.isEmpty() ) break;
                System.out.println("Processing row " + (rowIndex + 1) + " for employee: " + name);
                String magazin = row.getCell(1).getStringCellValue();
                String period;
                if (row.getCell(2).getCellType() == CellType.STRING) {
                    period = row.getCell(2).getStringCellValue();
                } else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                    int day = row.getCell(2).getDateCellValue().getDate();
                    period = day + "-" + day;
                } else {
                    period = "";
                }


                System.out.println("Vacation Period: " + period);
                String[] parts = period.split("\\*");
                String firstDay = parts[0].trim().replaceFirst("^0+(?!$)", "");
                String lastDay = parts[1].trim().replaceFirst("^0+(?!$)", "");

                String reason = row.getCell(3).getStringCellValue();
                reason = switch (reason) {
                    case "co", "CO" -> "concediu";
                    //case "m", "M" -> "maternitate";
                    case "cm", "m", "CM", "M" -> "medical";
                    case "abs", "ABS" -> "absenta";
                    case "dem", "DEM" -> "demisie";
                    default -> "concediu";
                };
                Holiday holiday = new Holiday(Integer.parseInt(firstDay), Integer.parseInt(lastDay), reason, name, magazin);
                holidays.add(holiday);
            }

            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }

            System.out.println("Excel file modified successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }

        for (Holiday holiday : holidays) {
            System.out.println("Holiday: " + holiday.getName() + ", " + holiday.getFirstDay() + "-" + holiday.getLastDay() + ", " + holiday.getReason());
        }

        return holidays;
    }
}
