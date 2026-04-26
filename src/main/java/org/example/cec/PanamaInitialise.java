package org.example.cec;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.example.cec.NormalizeName.set;

public class PanamaInitialise {


    private File panamaSheet;

    PanamaInitialise(File panamaSheet){
        this.panamaSheet = panamaSheet;
    }



    Map<String, PanamaShift> InitialisePanamaShifts() {
        // the key is the name of the employee

        String filePath = panamaSheet.getAbsolutePath();

        Map<String , PanamaShift> List = new java.util.HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for(int i = 0 ; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
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

}
