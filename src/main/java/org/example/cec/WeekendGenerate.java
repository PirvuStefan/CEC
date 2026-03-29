package org.example.cec;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.example.cec.WeekendModify.checkColor;
import static org.example.cec.WeekendShift.whatDay;

public class WeekendGenerate {
    int[][] x,y;

    WeekendGenerate(int[] numberOfShifts, int[] pos, List<Employee> employees, File weekendSheet){
        x = new int[numberOfShifts.length][WeekendShift.size];
        y = new int[employees.size()][WeekendShift.sarbatoriSize];
        generateShift(x, numberOfShifts, pos);
        generateShiftEmployeesHolidays(employees, weekendSheet);

    }


    private int[][] generateShift(int[][] x, int[] numberOfShifts, int[] pos){

        // x[0] = generateFirstOne(workedSaturday[0], numberOfShifts[0], pos); // the first one is associated random to not be repetitive
        // do not generate the first one, might  be redundant
        for(int i = 0; i < x.length ; i++){
            x[i] = generateLine(x, i, numberOfShifts, pos);
        }


        return x;
    }

    private int[] generateLine(int[][] x, int lineIndex, int[] numberOfShifts, int[] pos) {
        int[] v = new int[WeekendShift.size];
        int minim = calculateMin(x, lineIndex); // calculate the minimum number of shifts assigned to any day so far
        int tries = 0;


        do {
            boolean loop = false;
            if (numberOfShifts[lineIndex] == 0) return v;

            for (int i = 0; i < v.length; i++) {
                if (numberOfShifts[lineIndex] == 0) return v;
                int count = 0;
                for (int j = 0; j < lineIndex; j++) {
                    if (x[j][i] == 1) count++;
                }

                if (count == minim) {

                    if(whatDay(pos[i], pos).equals("sambata") && v[i + 1] == 0 && pos[i+1] - 1 == pos[i] && i < WeekendShift.size - 1 && v[i] == 0){ // if is a saturday and the day before was not a shift and is indeed a friday
                        v[i] = 1;
                        if(--numberOfShifts[lineIndex] == 0) return v;
                        loop = true;
                    }
                    else if (i > 0 && whatDay(pos[i], pos).equals("duminica") && v[i - 1] == 0 && pos[i - 1] + 1 == pos[i] && v[i] == 0) { // if is a sunday and the day before was not a shift and is indeed a saturday
                        v[i] = 1;
                        if(--numberOfShifts[lineIndex] == 0) return v;
                        loop = true;
                    }


                }

            }
            if(!loop) minim++; // if we did not assign any shift in this iteration, we increase the minimum to allow more flexibility

            if( tries++ > 100){
                System.out.println("Cannot assign shifts for employee at line index " + lineIndex);
                return v;
            }
        }while(numberOfShifts[lineIndex] > 0);



        return v;
    }

    private int calculateMin(int[][] x, int lineIndex){
        int minim = Integer.MAX_VALUE;
        for(int j = 0; j < x[0].length; j++) {
            int count = 0;
            for (int i = 0; i < lineIndex; i++) if (x[i][j] == 1) count++;

            if (count < minim) minim = count;
        }
        return minim;
    }



    private int[][] generateShiftEmployeesHolidays(List< Employee > employees, File weekendSheet){
        int[][] y = new int[employees.size()][WeekendShift.sarbatoriSize];
        try {
            FileInputStream fis = new FileInputStream(weekendSheet);
            Workbook workbook = WorkbookFactory.create(fis); // Updated to use WorkbookFactory.create
            Sheet sheet = workbook.getSheetAt(0);

            Row checkRow = sheet.getRow(1);
            for( int i = 2; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if( row == null ) break;
                String name = row.getCell(1).getStringCellValue();
                if( name == null || name.isEmpty() ) break;
                name = name.toLowerCase();
                for( int j = 0; j < employees.size(); j++){
                    if( employees.get(j).name.toLowerCase().equals(name)){
                        int hol = 0;
                        for (int k = 2; k < 30; k++) {
                            if(row.getCell(k) == null ) break;
                            if(!checkColor(checkRow.getCell(k))){


                                if (row.getCell(k).getCellType() == CellType.BLANK) y[j][hol++] = 0;
                                else y[j][hol++] = 1;
                            }
                            if( hol == WeekendShift.sarbatoriSize ) break;
                        }

                    }


                }



            }

            for(int j = 0;j< employees.size(); j++){
                System.out.print("Employee: " + employees.get(j).name);
                System.out.println();
                for(int k = 0; k < WeekendShift.sarbatoriSize; k++){
                    System.out.print(y[j][k] + " ");
                }
                System.out.println();
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return y;




    }
}
