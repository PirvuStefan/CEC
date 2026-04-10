package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.WorkingHoursTotal;

import static org.example.cec.Placeholders.ABSENTEE_OFFSET;
import static org.example.cec.Placeholders.WORKING_OFFSET;

class AbsentaHoliday extends Holiday {

    AbsentaHoliday(int firstDay, int lastDay, String name, String magazin) {
        super(firstDay, lastDay, name, magazin);
    }

    @Override
    public String getReason() { return "absenta"; }

    @Override
    protected void applyColor(CellStyle style, Cell cell, Row row) {
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
    }

    @Override
    protected void updateSummary(Row row, Row headerRow, int daysInMonth) {
        Cell awayCell = row.getCell(daysInMonth + ABSENTEE_OFFSET.asInt());
        if (awayCell == null) {
            awayCell = row.createCell(daysInMonth + ABSENTEE_OFFSET.asInt(), CellType.STRING);
        }
        String value = getAwayValue(headerRow, row, firstDay, lastDay, awayCell);
        row.getCell(daysInMonth + ABSENTEE_OFFSET.asInt()).setCellValue(value);
        row.getCell(daysInMonth + WORKING_OFFSET.asInt()).setCellValue(WorkingHoursTotal.get(row));
    }
}
