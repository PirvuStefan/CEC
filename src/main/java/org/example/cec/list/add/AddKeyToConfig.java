package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.example.cec.list.EmployeeRowList;
import org.example.cec.list.ListConfig;
import org.example.cec.ui.validate.AlertUtility;

import java.io.File;

public class AddKeyToConfig implements FreePosition, AddEmployeeToRow {

    //class to add the key after a successful add employee process
    String employeeName;
    int key;

    AddKeyToConfig(String employeeName, int key){
        this.employeeName = employeeName;
        this.key = key;
    }


    @Override
    public int getPos(Sheet sheet) {


            int i;

            for (i = EmployeeRowList.EMPLOYEE_START_POS; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) break;

                Cell cell = row.getCell(1);
                String name = (cell != null) ? cell.getStringCellValue().trim() : "";

                if (name.isEmpty()) break;


            }
            return i;

        }

    @Override
    public void set(Row row) {
        row.createCell(0).setCellValue(employeeName);
        row.createCell(1).setCellValue(key);
    }

    public void setRow(Sheet sheet){
        int pos = getPos(sheet);
        Row row = sheet.createRow(pos);
        set(row);
    }
}
