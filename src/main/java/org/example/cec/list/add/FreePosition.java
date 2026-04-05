package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class FreePosition {

    static int get(Sheet sheet){
        int i;

        for (i = 495; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            if (row == null) break;

            Cell cell = row.getCell(0);
            String name = (cell != null) ? cell.getStringCellValue().trim() : "";

            if (name.isEmpty()) break;


            System.out.println("Found existing data: " + name);
        }
        return i;
    }
}
