package org.example.cec.list;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.cec.CellValueGetter;
import org.example.cec.NormalizeName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SearchEmployee implements CellValueGetter {
    private int key = -1;

    public SearchEmployee(String nameSearch) {

        ListConfig listConfig = new ListConfig();
        File file = listConfig.getFile();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis,listConfig.password.getValue())) {

            Sheet sheet = workbook.getSheetAt(ListSheet.EMPLOYEE_SEARCH.asInt());




            for(int i = 0 ; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) break;
                String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                int keyNow = getValueint(row, 2);
                if(name == null || name.isEmpty()) continue;
                if(name.equals(nameSearch)) {
                  this.key = keyNow;
                  return;
                }



            }








            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }



        } catch (IOException e) { // Added InvalidFormatException
            e.printStackTrace();
        }





    }

    public int getKey() {
        return key;
    }




}

