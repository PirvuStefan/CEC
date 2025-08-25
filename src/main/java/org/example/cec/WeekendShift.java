package org.example.cec;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class WeekendShift {

    static int size;
    static int[] pos;
    boolean[] work;
    public void initialiseDays(int size){

        WeekendShift.size = size;
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
            while (row.getCell(count + 4) != null){
                pos[ count ] = (int) row.getCell(count + 4).getNumericCellValue();
                count++;

            }
                // +4 deoarece primele 4 coloane sunt magazin, nume, numarul de shift uri si daca a lucrat sambata in luna precedenta




        } catch (Exception e) {
            e.printStackTrace();
        }
        count--;
        size = count;
    }



}
