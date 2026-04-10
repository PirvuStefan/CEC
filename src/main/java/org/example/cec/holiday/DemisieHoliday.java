package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;

class DemisieHoliday extends Holiday {

    DemisieHoliday(int firstDay, int lastDay, String name, String magazin) {
        super(firstDay, lastDay, name, magazin);
    }

    @Override
    public String getReason() { return "demisie"; }

    @Override
    protected boolean skipsWeekend() { return false; }

    @Override
    protected void applyColor(CellStyle style, Cell cell, Row row) {
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
    }

    // Color the employee name cell red before processing the holiday days
    @Override
    protected void beforeRange(Row row, Row headerRow) {
        Cell nameCell = row.getCell(2);
        CellStyle nameStyle = row.getSheet().getWorkbook().createCellStyle();
        nameStyle.cloneStyleFrom(nameCell.getCellStyle());
        nameStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        nameCell.setCellStyle(nameStyle);
    }

    // After the resignation date, color the rest of the month red (weekends are skipped here)
    @Override
    protected void afterRange(Row row, Row headerRow, int daysInMonth) {
        applyRange(row, headerRow, lastDay + 1, daysInMonth, true);
    }
}
