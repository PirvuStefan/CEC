package org.example.cec;

import org.apache.poi.ss.usermodel.*;
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
        for( int i = 0 ; i <= daysInMonth + 4 ; i++){
            Cell cell = sheet.getRow(rowIndex).getCell(i);
            if(cell != null){
                cell.setCellValue("");
            }
        }
    }


    private static String checkColorValability(Cell cell){

        // here we check if the color is white , bluemarin
        String s;
        if( cell == null ) return null;
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }

        if( rgbHex.equals("FFFFF")) return "WHITE";
        if( rgbHex.equals("002060")) return "BLUEMARIN";
        if( rgbHex.equals("FFFF00")) return "YELLOW";
        if( rgbHex.equals("00FFFF")) return "AQUA";
        return "OTHER";



    }
}
