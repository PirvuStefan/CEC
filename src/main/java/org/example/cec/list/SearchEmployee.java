// java
package org.example.cec.list;

import org.apache.poi.EncryptedDocumentException;
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

public class SearchEmployee implements CellValueGetter, NormalizeName {
    private int key = -1;

    public SearchEmployee(String nameSearch) {
        ListConfig listConfig = new ListConfig();
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

        boolean modified = false;
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

                String normSearch = NormalizeName.set(nameSearch).replaceAll("[\\s\\-]+", "");

                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue; // skip empty rows safely

                    String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                    int keyNow = getValueint(row, 2);
                    if (name == null || name.isEmpty()) continue;

                    String normName = NormalizeName.set(name).replaceAll("[\\s\\-]+", "");
                    if (compareName(normName, normSearch)) {
                        this.key = keyNow;
                        break;
                    }
                }

                // only write back if workbook was modified (keeps behavior safe)
                if (modified) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        wb.write(fos);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean compareName(String name, String search) {

        if (name.equals(search)) return true;


        name = name.replace("-", " ");
        if (name.trim().equals(search)) return true;


        name = name.replaceAll("[()]", "");
        if (name.trim().equals(search)) return true;


        name = name.replaceAll("\\s+", " ");
        return name.trim().equalsIgnoreCase(search);
    }

    public int getKey() {
        return key;
    }
}
