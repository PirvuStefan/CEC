package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.example.cec.Placeholders.*;
import static org.example.cec.Placeholders.WORKING_OFFSET;
import static org.example.cec.WeekendShift.whatDay;
import static org.example.cec.ui.MainScene.daysInMonth;
import static org.example.cec.WeekendModify.checkColor;

public class WeekendModifyEmployee {


    static File launch(File mainSheet, String employeeName, int shifts[], int[] pos, int shiftsSarbatori[], int[] sarb) {
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
                    System.out.println("Found employee: " + name + " at row " + (rowIndex + 1));


                    ParseIndividualHours ParseNow = new ParseIndividualHours(row);
                    ParseNow.parse();
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
