package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.example.cec.HolidayModify.getWorkingHoursTotal;

public class ParseWorkingHours {





    static void initializeSheet(File mainSheet, int daysInMonth) {
        if( mainSheet == null || !mainSheet.exists() ) {
            throw new IllegalArgumentException("Main sheet file is null or does not exist.");
        }

        if( testModify(mainSheet, daysInMonth) ) ModifyMainWithDaily(mainSheet, daysInMonth);
        else System.out.println("No significant modifications needed in the main sheet.");

    }



    private static boolean testModify(File mainSheet, int daysInMonth) {

        int count = 0;
        int work = 0;
        boolean ok = false;

        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;

                for(int i = 1; i <= daysInMonth; i++){
                    int colIndex = i + 4; // because we start from column F (index 5)
                    if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                    Cell cell = row.getCell(colIndex);
                    boolean skip = false;
                    boolean b = checkColor(cell);
                    if(!b) continue;

                    if (cell != null) {
                        String cellValue = "";
                        if (cell.getCellType() == CellType.STRING) {
                            cellValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            cellValue = String.valueOf((int) cell.getNumericCellValue());
                        }
                        if (cellValue.equals("8")) {
                            ok = true;
                            break;
                        }
                    }
                }
                if(ok) work++;
                count++;
                ok = false;




            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with daily shifts successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        if(count == 0 ) return true;
        return count > 0 && ((double) work / count) <= 0.15;

    }

    private static File ModifyMainWithDaily(File mainSheet, int daysInMonth) {
        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;
                if (!checkWorking(row.getCell(2))) continue; // if the employee is currently not working, we skip him


                int startDay = 1;

                for (int i = 1; i <= daysInMonth; i++) {
                    Cell cell = row.getCell(i + 4);
                    if (cell != null) {
                        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
                        String rgbHex = "#FFFFFF"; // default white color
                        if (color != null) {
                            String hexColor = color.getARGBHex();
                            rgbHex = hexColor.substring(2, 8); // remove alpha channel
                            rgbHex = "#" + rgbHex.toUpperCase();
                        }
                        // Bluemarin (navy blue) is usually #000080
                        if (rgbHex.equals("#002060") && cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() == 8) {
                            startDay = i;
                            // Add your logic here for bluemarin cells with value 8
                        } else if (rgbHex.equals("#002060") && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("8")) {
                            startDay = i;
                        } else if (rgbHex.equals("#002060")) startDay = i;
                    }
                }

                for (int i = startDay; i <= daysInMonth; i++) {
                    int colIndex = i + 4; // because we start from column F (index 5)
                    if (colIndex >= row.getLastCellNum()) break; // skip if column index is out of bounds
                    Cell cell = row.getCell(colIndex);
                    if (!checkColor(cell)) continue; // if the cell is not white, we skip it

                    if (cell == null) {
                        cell = row.createCell(colIndex, CellType.STRING);
                    }
                    cell.setCellValue("8"); // set the cell value to 8 (daily shift)
                }

                if (startDay > 0) System.out.println("Employee: " + name + " , start day: " + startDay);

                row.getCell(daysInMonth + 5).setCellValue(getWorkingHoursTotal(row));


            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with daily shifts successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return mainSheet;
    }

    private static boolean checkWorking( Cell cell){
        String s;
        if( cell == null ) s =  "#FFFFFF";
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return ( rgbHex.equals("#FFFFFF") || rgbHex.equals("#00FFFF") ); // white or aqua
    }

    private static boolean checkColor( Cell cell ){
        String s;
        if( cell == null ) return true;
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return ( rgbHex.equals("#FFFFFF") || rgbHex.equals("#002060") ); // white or bluemarin
    }


}
