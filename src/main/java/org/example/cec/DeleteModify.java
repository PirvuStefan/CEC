package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class DeleteModify {

    static File Launch(File mainSheet, String center, int daysInMonth) {

        try (FileInputStream fis = new FileInputStream(mainSheet);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for(int i = 0 ; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) break;
                String name = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : "";
                if (name == null || name.isEmpty()) break;
                name = name.trim().toUpperCase();
                if (name.isEmpty()) break;

                String magazin = (row.getCell(3) != null) ? row.getCell(3).getStringCellValue() : "";
                if (magazin == null || magazin.isEmpty()) break;
                magazin = magazin.trim().toUpperCase();
                if( magazin.equals(center) ){
                    //call delete function for this row
                }


            }








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

    void deleteRow(Sheet sheet, int rowIndex, int daysInMonth) {
        // we aim to delete the progress from that row

        Row row = sheet.getRow(rowIndex);
        for( int i = 0 ; i <= daysInMonth + 4 ; i++){
            Cell cell = sheet.getRow(rowIndex).getCell(i);

            if(cell == null) continue;


            if( i >= 5 ){ // from column F to the end of month
                String colorStatus = checkColorValability(cell);
                if( colorStatus.equals("DELETE_COLOR") ){
                    // we do set the cell to white color
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



        }
        resetHoursWorked(row, daysInMonth);



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
        if (rgbHex.equals("#002060"))
            return "BLUEMARIN";
        return "DELETE_PROGRESS"; // white, yellow or purple ( normal day, weekend, holiday progress)
    }

    private static void resetHoursWorked(Row row, int daysInMonth) {

        row.getCell(daysInMonth + 5).setCellValue(""); // reset total hours
        row.getCell(daysInMonth + 6).setCellValue(""); // reset CM days
        row.getCell(daysInMonth + 7).setCellValue(""); // reset CO days
        row.getCell(daysInMonth + 9).setCellValue(""); // reset weekend shifts worked
        row.getCell(daysInMonth + 10).setCellValue(""); // reset sarbatoai shifts worked
    }
}
