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

public class AddToMain implements AddEmployeeToRow, CellValue, FreePosition {

    // adaugare la fisierul de pontaj pe luna curenta
    Person person;
    File file;
    AddToMain(Person person, File file){
        this.person = person;
        this.file = file;
    }

    public void start(){

        Workbook workbook = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = WorkbookFactory.create(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (Workbook wb = workbook) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) return;

            int i = getPos(sheet);

            set(sheet.createRow(i));

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
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
        row.createCell(4).setCellValue(Integer.parseInt(person.getCNP()));
        setHire(row, person.getEmploymentDate());


        row.createCell(daysInMonth + WORKING_OFFSET.asInt()).setCellValue(WorkingHoursTotal.get(row));
        row.createCell(daysInMonth + SALARY_OFFSET.asInt()).setCellValue(person.getSalary());






    }

    private void setHire(Row row, LocalDate hireDate){

        hireDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        int hirePos = hireDate.getDayOfMonth();

        Cell cell = row.createCell(hirePos + WORKING_OFFSET.asInt());

        Workbook workbook = row.getSheet().getWorkbook();
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        byte[] rgb = new byte[]{(byte)0, (byte)32, (byte)96}; // #002060
        XSSFColor color = new XSSFColor(rgb, null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);
    }

    @Override
    public int getPos(Sheet sheet) {

            int i;

            for (i = 10; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) break;

                Cell cell = row.getCell(2);
                String name = (cell != null) ? cell.getStringCellValue().trim() : "";

                if (name.isEmpty()) break;


            }
            return i;

    }
}
