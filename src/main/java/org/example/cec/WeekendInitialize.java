package org.example.cec;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.example.cec.WeekendModify.checkColor;

public class WeekendInitialize {

    private File weekendSheet;

    public WeekendInitialize(File weekendSheet) {
        this.weekendSheet = weekendSheet;
    }


    protected Map< String, List<Employee>> initialiseWeekendList() {
        System.out.println("Initialising weekend employees from file: " + weekendSheet.getAbsolutePath());
        try (FileInputStream fis = new FileInputStream(weekendSheet);
             Workbook workbook = WorkbookFactory.create(fis)) { // Updated to use WorkbookFactory.create

            Sheet sheet = workbook.getSheetAt(0);
            Map<String, List<Employee>> weekendEmployees = new HashMap<>();
            String magazin = "";
            List < Employee > employees = new ArrayList<>();



            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row firstRow = sheet.getRow(2);
                Row row = sheet.getRow(rowIndex);
                Row nextRow = sheet.getRow(rowIndex + 1);
                if (row == null) break;
                // every type of this document should end with 2 empty rows in order to work

                String name = row.getCell(1).getStringCellValue();
                if( name == null || name.isEmpty() ) break;
                if(!row.getCell(0).getStringCellValue().isEmpty()) magazin = row.getCell(0).getStringCellValue();





                WeekendShift shift = new WeekendShift();
                shift.initialiseDays(WeekendShift.size);
                int numberOfShifts = 0;
                for(int i = 0 ; i < 30; i++){
                    Cell cell = row.getCell(i + 2);
                    if(cell == null) break;
                    if( !checkColor(firstRow.getCell(i)) ) continue;
                    else if(cell.getCellType() == CellType.STRING){
                        if(cell.getStringCellValue().equals("X") || cell.getStringCellValue().equals("x")) numberOfShifts++;
                    }
                }
                //System.out.println(name + " has " + numberOfShifts + " shifts.");
                Employee employee = new Employee(name, numberOfShifts, shift);
                employees.add(employee);
                if( nextRow.getCell(0).getStringCellValue().isEmpty() && nextRow.getCell(1).getStringCellValue().isEmpty()){
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                    break;
                }
                if(!nextRow.getCell(0).getStringCellValue().isEmpty() ) {
                    weekendEmployees.put(magazin, new ArrayList<>(employees));
                    employees.clear();
                }

            }

            return weekendEmployees;

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return new HashMap<>(); // return an empty map if there was an error
    }

}
