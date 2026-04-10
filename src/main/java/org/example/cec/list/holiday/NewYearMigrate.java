package org.example.cec.list.holiday;

import org.apache.poi.ss.usermodel.Row;
import org.example.cec.CellValue;
import org.example.cec.list.EmployeeColumnList;
import org.example.cec.list.ListConfig;
import org.example.cec.list.add.config.SalaryConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.example.cec.list.ListSheet;
import org.apache.poi.EncryptedDocumentException;

public class NewYearMigrate implements CellValue {


    public void updateRow(Row row){



    float sum = getValueFloat(row.getCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR)) + getValueFloat(row.getCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_LAST_YEARS));


    row.createCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_LAST_YEARS).setCellValue(sum);
    row.createCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR).setCellValue(0);

    row.createCell(EmployeeColumnList.HOLIDAY_PERIODS).setCellValue("");
    row.createCell(EmployeeColumnList.HOLIDAY_NUMBER_USED_CURRENT_YEAR).setCellValue(0);
    row.createCell(EmployeeColumnList.HOLIDAY_NUMBER_USED).setCellValue(0);
    }

    // this should only be called when a new year starts, to migrate the remaining holidays of the employee to the last years left column, and reset the used holidays for the new year
    public void update(){
        File listFile = new File(ListConfig.getInstance().getFile().toURI());
        
        String pwd = null;
        try {
            if (ListConfig.getInstance().password != null) {
                pwd = ListConfig.getInstance().password.getValue();
            }
        } catch (Exception ignored) {
        }
        
        try (FileInputStream fis = new FileInputStream(listFile)) {
            Workbook workbook;
            workbook = getSheetsWithPassword(listFile, pwd, fis);

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                if (sheet != null) {
                    for (int i = 7; i <= sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) break;
                        
                        Cell firstCell = row.getCell(0);
                        if (firstCell == null || firstCell.getCellType() == org.apache.poi.ss.usermodel.CellType.BLANK) {
                            if (row.getCell(1) == null || row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.CellType.BLANK) {
                                break;
                            }
                        }
                        
                        if (firstCell != null && firstCell.getCellStyle() != null) {
                            short fgColor = firstCell.getCellStyle().getFillForegroundColor();
                            if (fgColor == IndexedColors.YELLOW.getIndex()) {
                                continue;
                            }
                        }
                        
                        updateRow(row);
                    }
                    
                    try (FileOutputStream fos = new FileOutputStream(listFile)) {
                        wb.write(fos);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Workbook getSheetsWithPassword(File listFile, String pwd, FileInputStream fis) throws IOException {
        Workbook workbook;
        try {
            if (pwd != null && !pwd.isEmpty()) {
                workbook = WorkbookFactory.create(fis, pwd);
            } else {
                workbook = WorkbookFactory.create(fis);
            }
        } catch (EncryptedDocumentException ede) {
            if (pwd != null && !pwd.isEmpty()) {
                workbook = WorkbookFactory.create(listFile, pwd);
            } else {
                throw ede;
            }
        }
        return workbook;
    }
}
