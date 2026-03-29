package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.cec.ui.MainScene.daysInMonth;
import static org.example.cec.WeekendShift.whatDay;
import static org.example.cec.Placeholders.*;

public class WeekendModify implements NormalizeName {

    private File mainSheet, weekendSheet;

    public WeekendModify(File mainSheet, File weekendSheet) {
        this.mainSheet = mainSheet;
        this.weekendSheet = weekendSheet;
    }

    public File launch(File mainSheet, File weekendSheet){

        Map< String , List<Employee>> weekendEmployees;
        System.out.println("Modifying main sheet with weekend shifts...");
        WeekendShift test = new WeekendShift();
        test.initialiseSize(weekendSheet); // to set the size of the weekend shift ( static variable);
        System.out.println("Weekend size: " + WeekendShift.size);
        WeekendInitialize initializer = new WeekendInitialize(weekendSheet);
        weekendEmployees = initializer.initialiseWeekendList();
        if( weekendEmployees.isEmpty()){
            System.out.println("Eroare la initializarea listei de angajati pentru weekend!");
        }
        else System.out.println("Lista de angajati pentru weekend a fost initializata cu succes!");

        //ParseWorkingHours.initializeSheet(mainSheet, daysInMonth);

        for( String magazin : weekendEmployees.keySet()){




            System.out.println("Magazin: " + magazin);
            System.out.print("----------------------\n");
            List < Employee > employees = weekendEmployees.get(magazin);
            int[] numberOfShifts = new int[employees.size()];
            for( int i = 0; i < employees.size(); i++) numberOfShifts[i] = employees.get(i).numberOfShifts;



            int[][] y;
            int[][] x = new int[employees.size()][WeekendShift.size];
            x = generateShift1(x, numberOfShifts, WeekendShift.pos); // generate the shifts for the employees
            y = generateShiftEmployeesHolidays( employees, weekendSheet); // generate the holidays for the employees


            for(int i = 0 ; i < x.length; i++){
                System.out.println("Employee: " + employees.get(i).name + " , number of shifts: " + employees.get(i).numberOfShifts );
                for(int j = 0; j < x[i].length; j++){
                    System.out.print(x[i][j] + " ");
                }
                System.out.print("\n");
            }




            for( int i = 0; i < employees.size(); i++){
                mainSheet = WeekendModifyEmployee(mainSheet, employees.get(i).name.toUpperCase(), x[i], WeekendShift.pos, y[i], WeekendShift.sarbatoare);
            }

        }


        System.out.println("Main sheet updated with weekend shifts successfully!");
        return mainSheet;
    }

    private static File WeekendModifyEmployee(File mainSheet, String employeeName, int shifts[], int[] pos, int shiftsSarbatori[], int[] sarb) {
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

                String normalizedMain = NormalizeName.set(name).replaceAll("[\\s\\-]+", "");
                String normalizedHoliday = NormalizeName.set(employeeName).replaceAll("[\\s\\-]+", "");

                if (normalizedMain.equals(normalizedHoliday)) {
                    // we found the employee, now we can modify the shifts

                    ParseIndividualHours.Parse(row);
                    // we parse the base working hours for the employees

                    int count = 0;
                    int nr = 0 ;
                    int sarbatoriCount = 0;
                    int firstDay = 0;

                    for(int i = 1; i <= daysInMonth; i++){
                        Cell cell = row.getCell(i + DAY_OFFSET.asInt());
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
                                firstDay = i;
                                // Add your logic here for bluemarin cells with value 8
                            }
                            else if(rgbHex.equals("#002060") && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("8")){
                                firstDay = i;
                            }
                        }
                    }
                    for (int i = 0; i < shifts.length; i++) {
                        if (shifts[i] == 1) {
                            nr++;
                            int day = pos[i];
                            int colIndex = day + DAY_OFFSET.asInt(); // because we start from column F (index 5)

                            boolean skip = false;

                            System.out.println("Days in month: " + daysInMonth);

                            if( day < firstDay ) continue;

                            if( whatDay(day, pos).equals("sambata") ){



                                for(int j = 0; j < WeekendShift.sarbatoriSize; j++){
                                    if (WeekendShift.sarbatoare[j] == day + 2 && day + 2 <= daysInMonth) {
                                        // if the saturday is a holiday, we skip it
                                        skip = true;
                                        break;
                                    }
                                }

                                if( day + 2 > daysInMonth ) skip = true;

                                if(!checkColor(row.getCell(colIndex + 2))) skip = true; // if the cell is not white, we skip it

                                if( day + 2 > daysInMonth ) skip = true;


                            }
                            else if( whatDay(day, pos).equals("duminica")){
                                for(int j = 0; j < WeekendShift.sarbatoriSize; j++){
                                    if (WeekendShift.sarbatoare[j] == day - 2 && day - 2 >= 1){

                                        skip = true;
                                        break;
                                    }
                                }

                                if( day - 2 < 1 ) skip = true;


                                if(!checkColor(row.getCell(colIndex - 2))) skip = true; // if the cell is not white, we skip it
                            }
                            if(skip) continue;
                            System.out.println("Modifying shift for " + employeeName + " on day " + day + " at column index " + colIndex + " with shift type " + whatDay(day, pos));




                            switch (whatDay(day, pos)) {
                                case "sambataF" -> row.getCell(colIndex).setCellValue(8);
                                case "duminicaF" -> row.getCell(colIndex).setCellValue(8);
                                case "sambata" -> {
                                    row.getCell(colIndex).setCellValue(8);
                                    if (day + 2 <= daysInMonth) row.getCell(colIndex + 2).setCellValue("");
                                    if (day + 1 <= daysInMonth) row.getCell(colIndex + 1).setCellValue("");
                                }
                                case "duminica" -> {
                                    row.getCell(colIndex).setCellValue(8);
                                    if (day > 2) row.getCell(colIndex - 2).setCellValue("");
                                    if (day > 1) row.getCell(colIndex - 1).setCellValue("");
                                }
                            }




                            // Set the cell style to light blue

                        }
                        if( nr >= 4 ) break; // if the employee has 4 shifts, we stop
                    }

                    for(int i = 0; i < pos.length; i++) {
                        Cell shiftCell = row.getCell(pos[i] + DAY_OFFSET.asInt());
                        String cellValue = "";
                        if (shiftCell != null) {
                            if (shiftCell.getCellType() == CellType.STRING) {
                                cellValue = shiftCell.getStringCellValue();
                            } else if (shiftCell.getCellType() == CellType.NUMERIC) {
                                cellValue = String.valueOf((int) shiftCell.getNumericCellValue());
                            }
                        }
                        if (cellValue.equals("8")) count++;
                    }

                    for(int i = 0; i < shiftsSarbatori.length; i++){
                        if( shiftsSarbatori[i] == 1 ){
                            int day = sarb[i];
                            int colIndex = day + DAY_OFFSET.asInt(); // because we start from column F (index 5)
                            if( colIndex >= row.getLastCellNum() ) break; // skip if column index is out of bounds
                            sarbatoriCount++;
                            row.getCell(colIndex).setCellValue(8);
                        }
                    }
//                    System.out.println(daysInMonth + " " + count + " " + sarbatoriCount);

                    if ( count == 0 && nr > 0 ) {
                        // if the count is 0, we do force a shift on the first available day after the firstDay,
                        count = getCount(pos, row, count, firstDay);
                    }
                    if( count == 1 && nr > 2){
                        // if the count is 1 and he has more than 2 shifts, we force another shift on the first available day after the firstDay
                        count = getCount(pos, row, count, firstDay);
                    }
                    if( count == 2 && nr > 3){
                        // if the count is 1 and he has more than 2 shifts, we force another shift on the first available day after the firstDay
                        count = getCount(pos, row, count, firstDay);
                    }


                    row.getCell(daysInMonth + WEEKEND_OFFSET.asInt()).setCellValue( Math.min(32, 8 * count) );
                    row.getCell(daysInMonth + SARBATORI_OFFSET.asInt()).setCellValue( Math.min(32, 8 * sarbatoriCount) );
                    row.getCell(daysInMonth + WORKING_OFFSET.asInt()).setCellValue(WorkingHoursTotal.get(row));

                    System.out.println("Main sheet updated with weekend shifts successfully! " + employeeName);break; // exit the loop after modifying the employee

                }
            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }



        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return mainSheet;
    }




    private static int[][] generateShiftEmployeesHolidays(List < Employee > employees, File weekendSheet){
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

    private static int[][] generateShift1(int[][] x, int[] numberOfShifts, int[] pos){

        // x[0] = generateFirstOne(workedSaturday[0], numberOfShifts[0], pos); // the first one is associated random to not be repetitive
        // do not generate the first one, might  be redundant
        for(int i = 0; i < x.length ; i++){
            x[i] = generateLine(x, i, numberOfShifts, pos);
        }


        return x;
    }

    private static int[] generateLine(int[][] x, int lineIndex, int[] numberOfShifts, int[] pos) {
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






    private static int calculateMin(int[][] x, int lineIndex){
        int minim = Integer.MAX_VALUE;
        for(int j = 0; j < x[0].length; j++) {
            int count = 0;
            for (int i = 0; i < lineIndex; i++) if (x[i][j] == 1) count++;

            if (count < minim) minim = count;
        }
        return minim;
    }

    static boolean checkColor(Cell cell){
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


    private static int getCount(int[] pos, Row row, int count, int firstDay) {
        for(int j = 0; j < WeekendShift.size; j++){
            if( whatDay(WeekendShift.pos[j], pos).equals("sambata") ){
                if( WeekendShift.pos[j] >= firstDay ){
                    int colIndex = WeekendShift.pos[j] + DAY_OFFSET.asInt();
                    if( colIndex >= row.getLastCellNum() ) break; // skip if column index is out of bounds
                    if( checkColor(row.getCell(colIndex)) && checkColor(row.getCell(colIndex + 2)) && !row.getCell(colIndex).getStringCellValue().equals("8") && !row.getCell(colIndex + 1).getStringCellValue().equals("8") && WeekendShift.pos[j] + 2 <= daysInMonth ){
                        row.getCell(colIndex).setCellValue(8);
                        count++;
                        break;
                    }
                }
            }
            else if( whatDay(WeekendShift.pos[j], pos).equals("duminica") ){
                if( WeekendShift.pos[j] >= firstDay ){
                    int colIndex = WeekendShift.pos[j] + DAY_OFFSET.asInt();
                    if( colIndex >= row.getLastCellNum() ) break; // skip if column index is out of bounds
                    if( checkColor(row.getCell(colIndex)) && checkColor(row.getCell(colIndex - 2)) && !row.getCell(colIndex).getStringCellValue().equals("8") && !row.getCell(colIndex - 1).getStringCellValue().equals("8") && WeekendShift.pos[j] - 2 >= 1 ){
                        row.getCell(colIndex).setCellValue(8);
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }






}
