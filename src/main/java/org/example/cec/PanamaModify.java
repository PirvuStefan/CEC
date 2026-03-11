package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.panama.Panama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.example.cec.HelloApplication.normalizeName;
import static org.example.cec.Placeholders.DAY_OFFSET;
import static org.example.cec.WeekendModify.checkColor;

public class PanamaModify {



    private static Map<String, PanamaShift> InitialisePanamaShifts(File panamaSheet) {
        // the key is the name of the employee

        String filePath = panamaSheet.getAbsolutePath();

        Map<String , PanamaShift> List = new java.util.HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for(int i = 0 ; i <= sheet.getLastRowNum(); i++) {
                Row firstRow = sheet.getRow(2);
                Row row = sheet.getRow(i);
                Row nextRow = sheet.getRow(i + 1);
                if (row == null) break;
                // every type of this document should end with 2 empty rows in order to work
                String name = row.getCell(1).getStringCellValue();
                if( name == null || name.isEmpty() ) break;

                for(int j = 2; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    String cellValue = cell.getStringCellValue();
                    if (cellValue == null || cellValue.isEmpty()) continue;

                    PanamaShift shift = new PanamaShift(panamaSheet, row);
                    List.put(name, shift);

                    // Process the cell value as needed
                    System.out.println("Processing cell value: " + cellValue);
                }



            }








            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }



        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return List;
    }

     static File launch(File mainSheet, File panamaSheet) {
        Map<String, PanamaShift> panamaShifts = InitialisePanamaShifts(panamaSheet);

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

                assert panamaShifts != null;
                if(panamaShifts.containsKey(name) || panamaShifts.containsKey(normalizeName(name))) {
                    PanamaShift shift = panamaShifts.get(name);
                    shift.setShift(row);


                    // Apply the Panama shifts to the main sheet as needed
                    // This is where you would implement the logic to modify the main sheet based on the Panama shifts
                }
            }

            try (FileOutputStream fos = new FileOutputStream(mainSheet)) {
                workbook.write(fos);
            }

            System.out.println("Main sheet updated with Panama shifts successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }

        return mainSheet;
    }
}
