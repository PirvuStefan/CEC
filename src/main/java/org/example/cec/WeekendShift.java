package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class WeekendShift {

    static int size;
    static int sarbatoriSize;
    static int[] pos;
    static int[] sarbatoare;

    public void initialiseDays(int size){
        WeekendShift.size = size;
        System.out.print("Marimea : " + size);
    }

    public void initialiseSize(File weekendFile) {
        int count = 0;
        int sarbatoriCount = 0;
        int position = 0;
        pos = new int[32];
        sarbatoare = new int[10];
        try (FileInputStream fis = new FileInputStream(weekendFile)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            while (row.getCell(count + 2) != null) {

                if(!checkColor(row.getCell(count + 2))){
                    System.out.println("NU E ALB " + getValueint(row, count + 2) ) ;
                    sarbatoare[sarbatoriCount++] = getValueint(row, count + 2);
                }
                else pos[position++] = getValueint(row, count + 2);
                count++;
                if( count > 15) break;
            }







        } catch (Exception e) {
            e.printStackTrace();
        }
        size = position - 2;
        System.out.println("SA VEDEM");
        for(int i = 0; i < size + 2; i++) {

            if( pos[i] == 0) {
                size = i ;
                break;
            }
            System.out.print(pos[i] + " ");
        }
        sarbatoriSize = sarbatoriCount;
    }

    private int getValueint(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return 0; // sau altă valoare implicită
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0; // sau altă valoare implicită
            }
        }
        return 0; // sau altă valoare implicită

    }

    private boolean checkColor( Cell cell ){
        String s;
        if( cell == null ) s =  "#FFFFFF";
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return rgbHex.equals("#FFFFFF");
    }



}


