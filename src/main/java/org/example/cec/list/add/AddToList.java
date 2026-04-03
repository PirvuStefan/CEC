package org.example.cec.list.add;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.cec.NormalizeName;
import org.example.cec.list.ListConfig;
import org.example.cec.list.ListSheet;
import org.example.cec.list.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddToList {


    protected void addToList(Person person) {
        ListConfig listConfig = ListConfig.getInstance();
        File file = listConfig.getFile();

        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalStateException("Excel file not found: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        String pwd = null;
        try {
            if (listConfig.password != null) {
                pwd = listConfig.password.getValue();
            }
        } catch (Exception ignored) {
            return;

        }


        Workbook workbook = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            try {
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(fis, pwd);
                } else {
                    workbook = WorkbookFactory.create(fis);
                }
            } catch (EncryptedDocumentException ede) {
                // if opening from stream failed due to encryption, try file-based open with password
                if (pwd != null && !pwd.isEmpty()) {
                    workbook = WorkbookFactory.create(file, pwd);
                } else {
                    throw ede;
                }
            }

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_SEARCH.asInt());
                if (sheet == null) return;


                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue; // skip empty rows safely

                    String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";

                }



            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
