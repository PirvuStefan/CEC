package org.example.cec.list.add;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.example.cec.CellValue;
import org.example.cec.Holiday;
import org.example.cec.NormalizeName;
import org.example.cec.WorkingHoursTotal;
import org.example.cec.list.ListSheet;
import org.example.cec.list.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import static org.example.cec.Placeholders.*;
import static org.example.cec.Placeholders.ABSENTEE_OFFSET;
import static org.example.cec.Placeholders.MEDICAL_OFFSET;
import static org.example.cec.Placeholders.WORKING_OFFSET;
import static org.example.cec.ui.MainScene.daysInMonth;

public class AddToMain implements AddEmployeeToRow, CellValue {

    // adaugare la fisierul de pontaj pe luna curenta
    Person person;
    File file;
    AddToMain(Person person, File file){
        this.person = person;
        this.file = file;
    }

    public void start(){

        try (FileInputStream fis = new FileInputStream(file)) {

            Workbook workbook = WorkbookFactory.create(fis);

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(0);
                if (sheet == null) return;

                int i = FreePosition.get(sheet);

                set(sheet.createRow(i));


                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void set(Row row) {

        Row prev = row.getSheet().getRow(row.getRowNum() - 1);

        row.createCell(1).setCellValue(person.getPlaceOfWork());
        row.createCell(2).setCellValue(person.getName());
        row.createCell(3).setCellValue( getValueint(prev,3) + 1 );
        row.createCell(4).setCellValue(person.getCNP());
        setHire(row, person.getEmploymentDate());


        row.createCell(daysInMonth + WORKING_OFFSET.asInt()).setCellValue(WorkingHoursTotal.get(row));
        row.createCell(daysInMonth + SALARY_OFFSET.asInt()).setCellValue(person.getSalary());






    }

    private void setHire(Row row, LocalDate hireDate){

        hireDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        int hirePos = hireDate.getDayOfMonth();

        Cell cell = row.createCell(hirePos + WORKING_OFFSET.asInt());

        cell.setCellValue(8);


        Workbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        byte[] rgb = new byte[]{(byte)0, (byte)32, (byte)96}; // #002060
        XSSFColor color = new XSSFColor(rgb, null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);
    }
}
