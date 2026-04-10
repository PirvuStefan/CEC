package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;

import static org.example.cec.Placeholders.MEDICAL_OFFSET;

class MedicalHoliday extends Holiday {

    MedicalHoliday(int firstDay, int lastDay, String name, String magazin) {
        super(firstDay, lastDay, name, magazin);
    }

    @Override
    public String getReason() { return "medical"; }

    @Override
    protected boolean skipsWeekend() { return false; }

    @Override
    protected void applyColor(CellStyle style, Cell cell, Row row) {
        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
    }

    @Override
    protected void updateSummary(Row row, Row headerRow, int daysInMonth) {
        String medicalCellValue;
        Cell medicalCell = row.getCell(daysInMonth + MEDICAL_OFFSET.asInt());
        if (medicalCell == null) {
            medicalCell = row.createCell(daysInMonth + MEDICAL_OFFSET.asInt(), CellType.STRING);
        }
        if (medicalCell.getCellType() == CellType.STRING) {
            medicalCellValue = medicalCell.getStringCellValue().trim();
            if (medicalCellValue.isEmpty()) {
                medicalCellValue = Integer.toString(lastDay - firstDay + 1);
            } else {
                try {
                    int current = Integer.parseInt(medicalCellValue);
                    medicalCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                } catch (NumberFormatException e) {
                    medicalCellValue = medicalCellValue + " + " + (lastDay - firstDay + 1);
                }
            }
        } else if (medicalCell.getCellType() == CellType.NUMERIC) {
            int current = (int) medicalCell.getNumericCellValue();
            medicalCellValue = Integer.toString(current + (lastDay - firstDay + 1));
        } else {
            medicalCellValue = Integer.toString(lastDay - firstDay + 1);
        }
        row.getCell(daysInMonth + MEDICAL_OFFSET.asInt()).setCellValue(medicalCellValue);
    }
}
