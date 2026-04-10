package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import static org.example.cec.Placeholders.HOLIDAY_OFFSET;

public class ConcediuHoliday extends Holiday {

    ConcediuHoliday(int firstDay, int lastDay, String name, String magazin) {
        super(firstDay, lastDay, name, magazin);
    }

    @Override
    public String getReason() { return "concediu"; }

    @Override
    protected void applyColor(CellStyle style, Cell cell, Row row) {
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0x00, 0xB0, 0x50), null));
    }

    @Override
    protected void updateSummary(Row row, Row headerRow, int daysInMonth) {
        Cell awayCell = row.getCell(daysInMonth + HOLIDAY_OFFSET.asInt());
        if (awayCell == null) {
            awayCell = row.createCell(daysInMonth + HOLIDAY_OFFSET.asInt(), CellType.STRING);
        }
        String value = getAwayValue(headerRow, row, firstDay, lastDay, awayCell);
        row.getCell(daysInMonth + HOLIDAY_OFFSET.asInt()).setCellValue(value);
    }
}
