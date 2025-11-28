package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.example.cec.HelloApplication.daysInMonth;
import static org.example.cec.HelloApplication.normalizeName;


public class DeleteModify {

    static File Launch(File mainSheet, String center) {

        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);


            center = normalizeName(center).replaceAll("[\\s\\-]+", "");
            System.out.println(center);

            for(int i = 0 ; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;

                String magazin = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                if (magazin == null || magazin.isEmpty()) break;
                magazin = normalizeName(magazin).replaceAll("[\\s\\-]+", "");
                if( magazin.equals(center) ){
                    //call delete function for this row
                    System.out.println("Deleting holidays for employee: " + name + " at store: " + magazin);
                    deleteRow(sheet, i);
                }


            }








            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Modifications completed successfully. ( Delete the progress for center: " + center + " )");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }

        return mainSheet;

    }

    static void deleteRow(Sheet sheet, int rowIndex) {
        // we aim to delete the progress from that row

        Row row = sheet.getRow(rowIndex);
        for( int j = 1 ; j <= daysInMonth  ; j++){
            int i = j + 4; // because the days start from column F (index 5)
            Cell cell = sheet.getRow(rowIndex).getCell(i);

            if(cell == null) continue;


            String colorStatus = checkColorValability(cell);
                if( colorStatus.equals("DELETE_COLOR") ){
                    // we do set the cell to white color
                    System.out.println("Deleting holidays for employee: " + cell.getStringCellValue() + " at store: " + i);
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(cell.getCellStyle());
                    XSSFColor whiteColor = new XSSFColor(new java.awt.Color(255, 255, 255), null);
                    ((XSSFCellStyle) newStyle).setFillForegroundColor(whiteColor);
                    newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(newStyle);
                }
                else if(colorStatus.equals("DELETE_PROGRESS")) {
                    // we do set the cell to empty
                    cell.setCellValue("");
                }




        }
        resetHoursWorked(row);



    }


    private static String checkColorValability(Cell cell){
        if (cell == null) return null;
        XSSFColor color = null;
        if (cell.getCellStyle() != null) {
            color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        }
        String rgbHex = "#FFFFFF";
        if (color != null) {
            String hexColor = color.getARGBHex();
            if (hexColor != null && hexColor.length() >= 8) {
                rgbHex = "#" + hexColor.substring(2, 8).toUpperCase();
            } else {
                byte[] rgb = color.getRGB();
                if (rgb != null && rgb.length == 3) {
                    rgbHex = String.format("#%02X%02X%02X", rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF);
                }
            }
        }

        // holiday colors -> DELETE
        if (rgbHex.equals("#00B050") || rgbHex.equals("#00FFFF") || rgbHex.equals("#FFA500") || rgbHex.equals("#FF0000"))
            return "DELETE_COLOR";
        if (rgbHex.equals("#002060") || rgbHex.equals("#FFFFFF")) // if we do have white or dark blue ( start day of the contract ) , we do absolutely nothing
            return "NOT_DELETE";
        return "DELETE_PROGRESS"; //yellow or purple ( normal day, weekend, holiday progress)
    }

    private static void resetHoursWorked(Row row) {

        row.getCell(daysInMonth + 5).setCellValue(""); // reset total hours
        row.getCell(daysInMonth + 6).setCellValue(""); // reset CM days
        row.getCell(daysInMonth + 7).setCellValue(""); // reset CO days
        row.getCell(daysInMonth + 9).setCellValue(""); // reset weekend shifts worked
        row.getCell(daysInMonth + 10).setCellValue(""); // reset sarbatoai shifts worked

        // i think we also should put the "8" on white cell that remain white after deleting the progress, as they are normal working days
        for( int i = 1 ; i <= daysInMonth  ; i++) {

            int colIndex = i + 4; // because we start from column F (index 5)
            Cell cell = row.getCell(colIndex);
            if (cell == null) continue;


        }

    }
}
