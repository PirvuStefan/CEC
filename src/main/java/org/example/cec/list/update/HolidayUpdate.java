package org.example.cec.list.update;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.CellValue;
import org.example.cec.NormalizeName;
import org.example.cec.holiday.ConcediuHoliday;
import org.example.cec.list.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HolidayUpdate implements CellValue {

    Map<String, ArrayList<ConcediuHoliday>> map = new HashMap<>();
    // the key is the name of the employee

    public void addHoliday(String name, ConcediuHoliday holiday) {
        map.computeIfAbsent(name, k -> new ArrayList<>()).add(holiday);
    }

    public void apply() {
        if (map.isEmpty()) return;

        File listFile = ListConfig.getInstance().getFile();
        String pwd = null;
        try {
            if (ListConfig.getInstance().password != null) {
                pwd = ListConfig.getInstance().password.getValue();
            }
        } catch (Exception ignored) {}

        int currentMonth = MonthsPlaceholders.getCurrent().asInt();

        try (FileInputStream fis = new FileInputStream(listFile)) {
            Workbook workbook = NewYearMigrate.getSheetsWithPassword(listFile, pwd, fis);

            try (Workbook wb = workbook) {
                Sheet sheet = wb.getSheetAt(ListSheet.EMPLOYEE_LIST.asInt());
                if (sheet == null) return;

                for (int i = EmployeeRowList.EMPLOYEE_START_POS; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) break;

                    Cell nameCell = row.getCell(EmployeeColumnList.NAME);
                    if (nameCell == null || nameCell.getCellType() == CellType.BLANK) break;

                    String rowName = nameCell.getStringCellValue().trim();
                    if (rowName.isEmpty()) break;

                    String normalizedRow = NormalizeName.set(rowName).replaceAll("[\\s\\-]+", "");

                    for (Map.Entry<String, ArrayList<ConcediuHoliday>> entry : map.entrySet()) {
                        String normalizedKey = NormalizeName.set(entry.getKey()).replaceAll("[\\s\\-]+", "");
                        if (!normalizedRow.equals(normalizedKey)) continue;

                        for (ConcediuHoliday holiday : entry.getValue()) {
                            int days = holiday.getLastDay() - holiday.getFirstDay() + 1;

                            // HOLIDAY_NUMBER_USED
                            int used = getValueInt(row, EmployeeColumnList.HOLIDAY_NUMBER_USED);
                            setIntCell(row, EmployeeColumnList.HOLIDAY_NUMBER_USED, used + days);

                            // HOLIDAY_PERIODS: append ",firstDay-lastDay.Month"
                            Cell periodsCell = row.getCell(EmployeeColumnList.HOLIDAY_PERIODS);
                            if (periodsCell == null) periodsCell = row.createCell(EmployeeColumnList.HOLIDAY_PERIODS, CellType.STRING);
                            String existing = periodsCell.getCellType() == CellType.STRING ? periodsCell.getStringCellValue().trim() : "";
                            String period = holiday.getFirstDay() + "-" + holiday.getLastDay() + "." + currentMonth;
                            periodsCell.setCellValue(existing.isEmpty() ? period : existing + ", " + period);

                            // Subtract from HOLIDAY_NUMBER_LEFT_LAST_YEARS first, overflow from HOLIDAY_NUMBER_LEFT_CURRENT_YEAR
                            int leftLast = getValueInt(row, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_LAST_YEARS);
                            int leftCurrent = getValueInt(row, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR);

                            int fromLast = Math.min(days, leftLast);
                            int fromCurrent = days - fromLast;


                            setIntCell(row, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_LAST_YEARS, leftLast - fromLast);
                            setIntCell(row, EmployeeColumnList.HOLIDAY_NUMBER_LEFT_CURRENT_YEAR, leftCurrent - fromCurrent);
                        }
                        break;
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(listFile)) {
                    wb.write(fos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setIntCell(Row row, int col, int value) {
        Cell cell = row.getCell(col);
        if (cell == null) cell = row.createCell(col, CellType.NUMERIC);
        if (cell.getCellType() == CellType.STRING) {
            cell.setCellValue(Integer.toString(value));
        } else {
            cell.setCellValue(value);
        }
    }
}