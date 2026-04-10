package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;

class MaternitateHoliday extends Holiday {

    MaternitateHoliday(int firstDay, int lastDay, String name, String magazin) {
        super(firstDay, lastDay, name, magazin);
    }

    @Override
    public String getReason() { return "maternitate"; }

    @Override
    protected void applyColor(CellStyle style, Cell cell, Row row) {
        style.setFillForegroundColor(IndexedColors.PINK.getIndex());
    }
}
