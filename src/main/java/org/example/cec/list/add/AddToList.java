package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.CellValue;
import org.example.cec.list.*;
import org.example.cec.list.holiday.NewYearMigrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AddToList implements AddEmployeeToRow, FreePosition, CellValue {

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
            workbook = NewYearMigrate.getSheetsWithPassword(file, pwd, fis);

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                if (sheet == null) return;



                setRow(sheet, getPos(sheet));

                try (FileOutputStream fileOut = new FileOutputStream("arhiva/list/list.xlsx")) {
                    workbook.write(fileOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setRow(Sheet sheet, int i) {

        if (isNewMonth) {
            Row monthRow = sheet.getRow(i);
            if (monthRow == null) monthRow = sheet.createRow(i);


            MonthsPlaceholders currentMonth = MonthsPlaceholders.getCurrent();
            NewMonthParser newMonthParser = new NewMonthParser(monthRow, currentMonth);
            newMonthParser.start();


            i++; // employee goes on next row only when month row is inserted
        }

        Row employeeRow = sheet.getRow(i);
        if (employeeRow == null) employeeRow = sheet.createRow(i);
        set(employeeRow);

    }



    @Override
    public void set(Row row) {
        if (row == null || person == null) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        row.createCell(EmployeeColumnList.NAME).setCellValue(person.getName());
        row.createCell(EmployeeColumnList.SALARY).setCellValue(person.getSalary());
        if (person.getEmploymentDate() != null) {
            row.createCell(EmployeeColumnList.EMPLOYMENT_DATE).setCellValue(person.getEmploymentDate().format(formatter));
        }
        row.createCell(EmployeeColumnList.CNP).setCellValue(Integer.parseInt(person.getCNP()));
        row.createCell(EmployeeColumnList.CONTRACT_TYPE).setCellValue("nedet");
        row.createCell(EmployeeColumnList.JOB).setCellValue(person.getJob());
        row.createCell(EmployeeColumnList.PLACE_OF_WORK).setCellValue(person.getPlaceOfWork());
        row.createCell(EmployeeColumnList.GESTIUNE).setCellValue(person.getGestiune());

        row.createCell(EmployeeColumnList.PHONE_NUMBER).setCellValue(person.getPhoneNumber());
        row.createCell(EmployeeColumnList.CI).setCellValue(person.getCI());
        row.createCell(EmployeeColumnList.DOMICILE).setCellValue(person.getDomicile());
        if (person.getValability() != null) {
            row.createCell(EmployeeColumnList.VALABILITY).setCellValue(person.getValability().format(formatter));
        }

        int marc;
        Row prev = row.getSheet().getRow(row.getRowNum() - 1);
        if(prev.getCell(EmployeeColumnList.MARK).getCellType() == CellType.BLANK){
            marc = getValueInt(prev,3) + 1;
        } else {
            marc = getValueInt(row,3) + 11;
        }
        row.createCell(EmployeeColumnList.MARK).setCellValue(marc);

    }

    @Override
    public int getPos(Sheet sheet) {


            int i;

            for (i = 8; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) break;

                Cell cell = row.getCell(0);
                String name = (cell != null) ? cell.getStringCellValue().trim() : "";

                if (name.isEmpty()) break;


            }
            return i;

    }
}
