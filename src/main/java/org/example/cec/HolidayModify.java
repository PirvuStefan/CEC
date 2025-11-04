package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import static org.example.cec.HelloApplication.daysInMonth;

public class HolidayModify {

    static File Launch(File mainSheet, File holidaysSheet) {
        List<Holiday> holidays;
        String filePath = holidaysSheet.getAbsolutePath();

        ParseWorkingHours.initializeSheet(mainSheet, daysInMonth);

        holidays = InitialiseHolidaysList(holidaysSheet);
        System.out.println("Total holidays loaded: " + holidays.size());

        // now we do have the holiday data, we can modify the mainSheet
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create



            Sheet sheet = workbook.getSheetAt(0);
            // Go to row 3 (index 3, since it's 0-based), starting from column F (index 5), and find the last column with an integer (day of month)
            Row headerRow = sheet.getRow(3);
            Row first = sheet.getRow(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue().trim().toUpperCase() : null;
                if (name == null || name.isEmpty()) break;
                //magazin = magazin.toLowerCase();
                for( Holiday holiday : holidays) {




                    String normalizedMain = normalizeName(name).replaceAll("[\\s\\-]+", "");
                    String normalizedHoliday = normalizeName(holiday.getName()).replaceAll("[\\s\\-]+", "");





                    if (normalizedMain.equals(normalizedHoliday)) {
                        // we found a match, we can modify the row
                        // now based on the reason of the holiday, we do color the row from the mainSheet ( at that specific employee, from the first day to the last day)
                        int firstDay = holiday.getFirstDay();
                        int lastDay = holiday.getLastDay();
                        String reason = holiday.getReason();

                        System.out.println(name.toLowerCase());



                        for (int i = firstDay; i <= lastDay; i++) {
                            if (i < 1 || i > 31) break;


                            if( i == firstDay && firstDay > 2 ) { // if the first day is greater than 2, we need to check if the day before the first day is a weekend day ( yellow color ) , if it is a weekend day we need to delete the shift from that day
                                XSSFColor colorBefore = (XSSFColor) headerRow.getCell(firstDay + 2).getCellStyle().getFillForegroundColorColor(); // two days before the first day of the holiday
                                if(colorBefore != null){
                                    String hexColorBefore = colorBefore.getARGBHex();
                                    String rgbHexBefore = hexColorBefore.substring(2, 8); // remove alpha channel
                                    rgbHexBefore = "#" + rgbHexBefore.toUpperCase();
                                    if (rgbHexBefore.equals("#FFFF00")) { // if is yellow, we delete the shift ( 8 0 0 ) , monday now is free and he cant work no more on that day
                                        headerRow.getCell(firstDay + 2).setCellValue("");
                                    }
                                }
                            }

                            int colIndex = i + 4; // because we start from column F (index 5)
                            if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds

                            Cell cell = row.getCell(colIndex);
                            if (cell == null) {
                                cell = row.createCell(colIndex, CellType.STRING);
                            }

                            // Skip weekends
                            XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
                            String rgbHex = "#FFFFFF"; // default white color
                            if (color != null) {
                                String hexColor = color.getARGBHex();
                                rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                rgbHex = "#" + rgbHex.toUpperCase();
                            }
                            if (headerRow.getCell(colIndex) != null && (  rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF") ) && !reason.equals("medical") && !reason.equals("demisie") ) {
                                cell.setCellValue("");// clear the cell value if it's weekend day bacause he got a holiday and he will not longer work that specific weekend shift if he is on holiday( holiday from 10 to 23 ,
                                // weekend is 12,13, he had a shift on Sunday, but the shift needs to be removed now since he got a holiday cannot work no anymore)
                                continue;
                            }

                            color = (XSSFColor) headerRow.getCell(colIndex + 2).getCellStyle().getFillForegroundColorColor();
                            rgbHex = "#FFFFFF"; // default white color
                            if (color != null) {
                                String hexColor = color.getARGBHex();
                                rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                rgbHex = "#" + rgbHex.toUpperCase();
                            }
                            if(headerRow.getCell(colIndex + 2) != null && rgbHex.equals("#FFFF00")) {
                                // if the cell is a weekend day, we skip it
                                headerRow.getCell(colIndex + 2).setCellValue("");
                            }

                            // Set the cell value to the reason
                            cell.setCellValue(""); // Clear the cell value by setting it to an empty string
                            // Create a new cell style
                            CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
                            newStyle.cloneStyleFrom(cell.getCellStyle());

                            // Set the cell style based on the reason
                            if (reason.equals("concediu")) {
                                newStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0x00, 0xB0, 0x50), null));
                            } else if (reason.equals("maternitate")) {
                                newStyle.setFillForegroundColor(IndexedColors.PINK.getIndex());
                            } else if (reason.equals("medical")) {
                                newStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());;
                            } else if (reason.equals("absenta")) {
                                newStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                            } else if (reason.equals("demisie")) {
                                newStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                                Cell demisieCell = row.getCell(2);
                                CellStyle demisieStyle = row.getSheet().getWorkbook().createCellStyle();
                                demisieStyle.cloneStyleFrom(demisieCell.getCellStyle());
                                demisieStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                                demisieCell.setCellStyle(demisieStyle);
                            }
                            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cell.setCellStyle(newStyle);
                        }

                        if( reason.equals("demisie") ){


                            for(int i = lastDay + 1; i<= daysInMonth; i++){

                                int colIndex = i + 4; // because we start from column F (index 5)
                                if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds

                                Cell cell = row.getCell(colIndex);
                                if (cell == null) {
                                    cell = row.createCell(colIndex, CellType.STRING);
                                }

                                // Skip weekends
                                XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
                                String rgbHex = "#FFFFFF"; // default white color
                                if (color != null) {
                                    String hexColor = color.getARGBHex();
                                    rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                    rgbHex = "#" + rgbHex.toUpperCase();
                                }
                                if (headerRow.getCell(colIndex) != null && ( rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF") )) {
                                    cell.setCellValue("");// clear the cell value if it's weekend day bacause he got a holiday and he will not longer work that specific weekend shift if he is on holiday( holiday from 10 to 23 ,
                                    // weekend is 12,13, he had a shift on Sunday, but the shift needs to be removed now since he got a holiday cannot work no anymore)
                                    continue;
                                }

                                color = (XSSFColor) headerRow.getCell(colIndex + 2).getCellStyle().getFillForegroundColorColor();
                                rgbHex = "#FFFFFF"; // default white color
                                if (color != null) {
                                    String hexColor = color.getARGBHex();
                                    rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                    rgbHex = "#" + rgbHex.toUpperCase();
                                }
                                if(headerRow.getCell(colIndex + 2) != null && rgbHex.equals("#FFFF00")) {
                                    // if the cell is a weekend day, we skip it
                                    headerRow.getCell(colIndex + 2).setCellValue("");
                                }

                                // Set the cell value to the reason
                                cell.setCellValue(""); // Clear the cell value by setting it to an empty string
                                // Create a new cell style
                                CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
                                newStyle.cloneStyleFrom(cell.getCellStyle());
                                newStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                                newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                cell.setCellStyle(newStyle);

                            }
                        }


                        System.out.println("Days in month: " + daysInMonth);
                        if( reason.equals("concediu") ){
                            String awayCellValue = "";
                            Cell awayCell = row.getCell(daysInMonth + 7);
                            if (awayCell == null) {
                                awayCell = row.createCell(daysInMonth + 7, CellType.STRING);
                            }
                            if (awayCell.getCellType() == CellType.STRING) {
                                awayCellValue = awayCell.getStringCellValue().trim();
                                if (awayCellValue.isEmpty()) {
                                    awayCellValue = Integer.toString(lastDay - firstDay + 1);
                                } else {
                                    try {
                                        int current = Integer.parseInt(awayCellValue);
                                        awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                                    } catch (NumberFormatException e) {
                                        awayCellValue = awayCellValue + " + " + (lastDay - firstDay + 1);
                                    }
                                }
                            } else if (awayCell.getCellType() == CellType.NUMERIC) {
                                int current = (int) awayCell.getNumericCellValue();
                                awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                            } else {
                                awayCellValue = Integer.toString(lastDay - firstDay + 1);
                            }

                            for(int i = firstDay; i <= lastDay; i++){
                                //for this one we do not neeed to count the weekend days here or the holidays
                                if( i < 1 || i > 31 ) break;
                                int colIndex = i + 4; // because we start from column F (index 5)
                                if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                                Cell cell = row.getCell(colIndex);
                                if( cell == null ) continue;

                                XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
                                String rgbHex = "#FFFFFF"; // default white color
                                if (color != null) {
                                    String hexColor = color.getARGBHex();
                                    rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                    rgbHex = "#" + rgbHex.toUpperCase();
                                }
                                if (headerRow.getCell(colIndex) != null && ( rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF") )) {
                                    // if is yellow or purple, we do not count it
                                    try {
                                        int current = Integer.parseInt(awayCellValue);
                                        awayCellValue = Integer.toString(current - 1);
                                    } catch (NumberFormatException e) {
                                        // if we cannot parse it, we do nothing
                                    }
                                }
                            }

                            System.out.println(" asta e " + row.getCell(daysInMonth + 7).getStringCellValue());
                            row.getCell(daysInMonth + 7).setCellValue(awayCellValue);

                        }
                        else if( reason.equals("medical")){
                            String medicalCellValue = "";
                            Cell medicalCell = row.getCell(daysInMonth + 6);
                            if (medicalCell == null) {
                                medicalCell = row.createCell(daysInMonth + 6, CellType.STRING);
                            }
                            if (medicalCell.getCellType() == CellType.STRING) {
                                medicalCellValue = medicalCell.getStringCellValue().trim();
                                if (medicalCellValue.isEmpty()) {
                                    medicalCellValue = Integer.toString(lastDay - firstDay + 1);
                                } else {
                                    try {
                                        int current = Integer.parseInt(medicalCellValue);
                                        medicalCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                                    } catch (NumberFormatException e) {
                                        medicalCellValue = medicalCellValue + " + " + (lastDay - firstDay + 1);
                                    }
                                }
                            } else if (medicalCell.getCellType() == CellType.NUMERIC) {
                                int current = (int) medicalCell.getNumericCellValue();
                                medicalCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                            } else {
                                medicalCellValue = Integer.toString(lastDay - firstDay + 1);
                            }

                            System.out.println(" asta e " + row.getCell(daysInMonth + 6).getStringCellValue());
                            row.getCell(daysInMonth + 6).setCellValue(medicalCellValue);
                        }
                        else if( reason.equals("absenta")){
                            String awayCellValue = "";
                            Cell awayCell = row.getCell(daysInMonth + 15);
                            if (awayCell == null) {
                                awayCell = row.createCell(daysInMonth + 15, CellType.STRING);
                            }
                            if (awayCell.getCellType() == CellType.STRING) {
                                awayCellValue = awayCell.getStringCellValue().trim();
                                if (awayCellValue.isEmpty()) {
                                    awayCellValue = Integer.toString(lastDay - firstDay + 1);
                                } else {
                                    try {
                                        int current = Integer.parseInt(awayCellValue);
                                        awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                                    } catch (NumberFormatException e) {
                                        awayCellValue = awayCellValue + " + " + (lastDay - firstDay + 1);
                                    }
                                }
                            } else if (awayCell.getCellType() == CellType.NUMERIC) {
                                int current = (int) awayCell.getNumericCellValue();
                                awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                            } else {
                                awayCellValue = Integer.toString(lastDay - firstDay + 1);
                            }

                            for(int i = firstDay; i <= lastDay; i++){
                                //for this one we do not neeed to count the weekend days here or the holidays
                                if( i < 1 || i > 31 ) break;
                                int colIndex = i + 4; // because we start from column F (index 5)
                                if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                                Cell cell = row.getCell(colIndex);
                                if( cell == null ) continue;

                                XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
                                String rgbHex = "#FFFFFF"; // default white color
                                if (color != null) {
                                    String hexColor = color.getARGBHex();
                                    rgbHex = hexColor.substring(2, 8); // remove alpha channel
                                    rgbHex = "#" + rgbHex.toUpperCase();
                                }
                                if (headerRow.getCell(colIndex) != null && ( rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF") )) {
                                    // if is yellow or purple, we do not count it
                                    try {
                                        int current = Integer.parseInt(awayCellValue);
                                        awayCellValue = Integer.toString(current - 1);
                                    } catch (NumberFormatException e) {
                                        // if we cannot parse it, we do nothing
                                    }
                                }
                            }

                            //System.out.println(" asta e " + row.getCell(daysInMonth + 15).getStringCellValue());
                            row.getCell(daysInMonth + 15).setCellValue(awayCellValue);
                        }


                    }
                }


                // Process each row here
                // System.out.println("Processing row " + (rowIndex + 1));
            } // to commit merge



            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with holidays successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        System.out.println("Holiday modification completed.");
        return mainSheet;
    }

    private static List<Holiday> InitialiseHolidaysList(File holidaysSheet) {
        List<Holiday> holidays = new ArrayList<>();
        String filePath = holidaysSheet.getAbsolutePath();

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

    private static String normalizeName(String s) {
        if (s == null) return "";
        // Normalize to composed form, replace NBSP with normal space, remove invisible chars
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC);
        t = t.replace('\u00A0', ' ');                // NBSP -> normal space
        t = t.replaceAll("[\\u200B\\uFEFF\\p{Cf}]", ""); // zero-width + other format chars
        t = t.replaceAll("[*?]", "");                // keep existing removal of * and ?
        // Remove diacritics, collapse multiple whitespace and trim
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.replaceAll("\\s+", " ").trim();
        return t.toUpperCase();
    }
}
