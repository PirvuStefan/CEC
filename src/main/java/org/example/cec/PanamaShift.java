package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.cec.panama.Panama;
import org.example.cec.panama.PanamaFriday;
import org.example.cec.panama.PanamaSunday;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import static org.example.cec.Placeholders.DAY_OFFSET;
import static org.example.cec.WeekendModify.checkColor;


public class PanamaShift extends WeekendShift {

    ArrayList<Panama> panamaList = new ArrayList<>();
    ArrayList<Boolean> sarbatoriList = new ArrayList<>();

    PanamaShift(File path,Row row){
       if(PanamaShift.size == -1) initialiseSize(path);

       try {
           FileInputStream fis = new FileInputStream(path);
           Workbook workbook = new XSSFWorkbook(fis);
           Sheet sheet = workbook.getSheetAt(0);
           Row firstRow = sheet.getRow(1);

           for(int i = 0 ; i < sarbatoriSize + size;i++){

               int currentDay = i+2;

               Cell cell = row.getCell(currentDay);
               if(cell == null) break;
               boolean granted = false;
               if(cell.getCellType() == CellType.STRING) {
                   if (cell.getStringCellValue().equals("X") || cell.getStringCellValue().equals("x")) granted = true;
               }

               if( checkColor(firstRow.getCell(currentDay)) ) {

                   int day1 = getValueint(firstRow, currentDay);

                   if(whatDay(day1, PanamaShift.pos).equals("duminicaF")) addPanama(granted, day1);
                   else if(whatDay(day1, PanamaShift.pos).equals("samabataF")) addPanama(granted, day1 + 1);
                   else{
                       if(row.getCell(currentDay+1) == null) continue;
                       granted = granted || (cell.getCellType() == CellType.STRING && (cell.getStringCellValue().equals("X") || cell.getStringCellValue().equals("x")));
                       addPanama(granted,day1 + 1);
                       i++;
                       // here we should be on Saturday, that is why we increment i, so with the for loop increment we will have on the next testing also Saturday/duminicaF
                   }

                   continue;
               }

               sarbatoriList.add(granted);






           }
       } catch (Exception e) {
           System.out.println("Error initializing PanamaShift: " + e.getMessage());
           e.printStackTrace();
       }
    }

    private void addPanama(boolean value, int position) {

     if(value) panamaList.add(new PanamaFriday(position));
     else panamaList.add(new PanamaSunday(position));

    }

    void print(){
        for(Panama p : panamaList){
            p.print();
        }
        System.out.println("-----------------------------");
        for(int i = 0; i < sarbatoriList.size(); i++){
            System.out.println("Sarbatoare " + WeekendShift.sarbatoare[i] + ": " + sarbatoriList.get(i));
        }
    }

    void setShift(Row row) {

        for (int i = 0; i < this.sarbatoriList.size(); i++) {
            if (Boolean.TRUE.equals(this.sarbatoriList.get(i))) {
                int colIndex = WeekendShift.sarbatoare[i] + DAY_OFFSET.asInt();
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    cell.setCellValue(11);
                }
            }
        }

        for (int idx = this.panamaList.size() - 1; idx >= 0; idx--) {
            this.panamaList.get(idx).setWeekShift(row);
        }
    }




}
