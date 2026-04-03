package org.example.cec.list.add;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.cec.NormalizeName;
import org.example.cec.list.ListConfig;
import org.example.cec.list.ListSheet;
import org.example.cec.list.MonthsPlaceholders;
import org.example.cec.list.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddToList implements AddEmployeeToRow{

    Person person;
    boolean isNewMonth;

    AddToList(Person person, boolean isNewMonth){
        this.person = person;
        this.isNewMonth = isNewMonth;
    }


    protected void start() {
        ListConfig listConfig = ListConfig.getInstance();
        File file = listConfig.getFile();

        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalStateException("Excel file not found: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        String pwd = listConfig.password.getValue();

        Workbook workbook;

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


                for (int i = 10; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (sheet.getRow(i) == null) continue; // skip empty rows safely

                    String name = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : "";
                    if(name.isEmpty()){
                        if(isNewMonth){
                            MonthsPlaceholders currentMonth = MonthsPlaceholders.getCurrent(); // Adjust as needed
                            NewMonthParser newMonthParser = new NewMonthParser(sheet.getRow(i++), currentMonth);
                            newMonthParser.start();
                        }


                    }

                }



            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void set(Row row) {
        if (row == null || person == null) {
            return;
        }


        row.createCell(0).setCellValue(person.getName());
        row.createCell(1).setCellValue(person.getCNP());
        row.createCell(2).setCellValue(person.getJob());
        row.createCell(3).setCellValue(person.getSalary());

        if (person.getEmploymentDate() != null) {
            row.createCell(4).setCellValue(person.getEmploymentDate().toString()); // Or format Date properly
        }

        row.createCell(5).setCellValue(person.getPhoneNumber());
        row.createCell(6).setCellValue(person.getGestiune());
        row.createCell(7).setCellValue(person.getPlaceOfWork());
        row.createCell(8).setCellValue(person.getDomicile());
    }
}
