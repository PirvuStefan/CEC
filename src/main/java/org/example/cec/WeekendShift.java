package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class WeekendShift {

    static int size;
    static int[] pos;
    boolean[] work;
    public void initialiseDays(int size){

        WeekendShift.size = size;
        System.out.print(size + " Marimea : ");
        work = new boolean[size];
        for(int i = 0; i < size; i++) work[i] = false;

    }

    public void initialiseSize(File weekendFile) {
        int count = 0;
        pos = new int[32];
        try (FileInputStream fis = new FileInputStream(weekendFile)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            while (row.getCell(count + 3) != null) pos[ count++] = getValueint(row, count + 3);


                // +4 deoarece primele 4 coloane sunt magazin, nume, numarul de shift uri si daca a lucrat sambata in luna precedenta




        } catch (Exception e) {
            e.printStackTrace();
        }
        size = count - 2;
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



}


