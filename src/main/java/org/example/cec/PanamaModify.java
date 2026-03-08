package org.example.cec;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PanamaModify {



    private static Map<String, PanamaShift> InitialiseHolidaysList(File panamaSheet) {
        // the key is the name of the employee

        String filePath = panamaSheet.getAbsolutePath();

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

                    // Process the cell value as needed
                    System.out.println("Processing cell value: " + cellValue);
                }



            }








            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }

            System.out.println("Excel file modified successfully!");

        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }
        return null;
    }
}
