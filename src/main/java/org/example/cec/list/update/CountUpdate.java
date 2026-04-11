package org.example.cec.list.update;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.CellValue;
import org.example.cec.list.EmployeeColumnList;
import org.example.cec.list.ListConfig;
import org.example.cec.list.ListSheet;
import org.example.cec.list.add.config.JsonConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CountUpdate implements CellValue {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    LocalDate lastUpdate;
    public static final double DAY_ADD = 0.055;

    CountUpdate() {
        try {
            this.lastUpdate = JsonConfig.getInstance().getLastUpdate();
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
            this.lastUpdate = LocalDate.now();
        }
    }

    private void updateRow(Row row) {
        Cell employmentCell = row.getCell(EmployeeColumnList.EMPLOYMENT_DATE);
        if (employmentCell == null) return;

        LocalDate employmentDate;
        try {
            employmentDate = LocalDate.parse(employmentCell.getStringCellValue(), DATE_FORMAT);
        } catch (Exception e) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = lastUpdate.isAfter(employmentDate) ? lastUpdate : employmentDate;

        if (!startDate.isBefore(today)) return;

        long days = ChronoUnit.DAYS.between(startDate, today);
        double toAdd = days * DAY_ADD;

        float current = getValueFloat(row.getCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR));
        row.createCell(EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR).setCellValue(current + toAdd);
    }

    public void update() {
        File listFile = new File(ListConfig.getInstance().getFile().toURI());

        String pwd = null;
        try {
            if (ListConfig.getInstance().password != null) {
                pwd = ListConfig.getInstance().password.getValue();
            }
        } catch (Exception ignored) {
        }

        try (FileInputStream fis = new FileInputStream(listFile)) {
            Workbook workbook = NewYearMigrate.getSheetsWithPassword(listFile, pwd, fis);

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                if (sheet == null) return;

                for (int i = 7; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) break;

                    Cell firstCell = row.getCell(0);
                    if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                        if (row.getCell(1) == null || row.getCell(1).getCellType() == CellType.BLANK) {
                            break;
                        }
                    }

                    if (firstCell != null && firstCell.getCellStyle() != null) {
                        if (firstCell.getCellStyle().getFillForegroundColor() == IndexedColors.YELLOW.getIndex()) {
                            continue;
                        }
                    }

                    updateRow(row);
                }

                try (FileOutputStream fos = new FileOutputStream(listFile)) {
                    wb.write(fos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}