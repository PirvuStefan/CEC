package org.example.cec.holiday;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import static org.example.cec.Placeholders.DAY_OFFSET;

public abstract class Holiday {

    protected final int firstDay;
    protected final int lastDay;
    protected int totalDays;
    protected final String name;
    protected final String magazin;

    protected Holiday(int firstDay, int lastDay, String name, String magazin) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.totalDays = lastDay - firstDay + 1;
        this.name = name;
        this.magazin = magazin;
    }

    public int getFirstDay() { return firstDay; }
    public int getLastDay() { return lastDay; }
    public int getTotalDays(){ return totalDays; }
    public String getName() { return name; }
    public String getMagazin() { return magazin; }
    public abstract String getReason();


    public static Holiday of(int firstDay, int lastDay, String reason, String name, String magazin) {
        return switch (reason) {
            case "maternitate" -> new MaternitateHoliday(firstDay, lastDay, name, magazin);
            case "medical"    -> new MedicalHoliday(firstDay, lastDay, name, magazin);
            case "absenta"    -> new AbsentaHoliday(firstDay, lastDay, name, magazin);
            case "demisie"    -> new DemisieHoliday(firstDay, lastDay, name, magazin);
            default           -> new ConcediuHoliday(firstDay, lastDay, name, magazin);
        };
    }

    // Template method — orchestrates the full processing of this holiday for one employee row
    public final void apply(Row row, Row headerRow, int daysInMonth) {
        beforeRange(row, headerRow);
        applyRange(row, headerRow, firstDay, lastDay, skipsWeekend());
        afterRange(row, headerRow, daysInMonth);
        updateSummary(row, headerRow, daysInMonth);
    }

    // Shared range-processing logic used by the template and by subclasses (e.g. demisie post-range)
    protected void applyRange(Row row, Row headerRow, int from, int to, boolean skipWeekends) {
        // Check if the Saturday linked to the first holiday day should be cleared
        if (from == firstDay && from > 2) {
            XSSFColor colorBefore = (XSSFColor) headerRow.getCell(from + 2).getCellStyle().getFillForegroundColorColor();
            if (colorBefore != null) {
                String hex = "#" + colorBefore.getARGBHex().substring(2, 8).toUpperCase();
                if (hex.equals("#FFFF00")) {
                    headerRow.getCell(from + 2).setCellValue("");
                }
            }
        }

        for (int i = from; i <= to; i++) {
            if (i < 1 || i > 31) break;

            int colIndex = i + DAY_OFFSET.asInt();
            if (colIndex >= row.getLastCellNum()) break;

            Cell cell = row.getCell(colIndex);
            if (cell == null) cell = row.createCell(colIndex, CellType.STRING);

            XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
            String rgbHex = "#FFFFFF";
            if (color != null) {
                rgbHex = "#" + color.getARGBHex().substring(2, 8).toUpperCase();
            }

            if (headerRow.getCell(colIndex) != null && (rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF")) && skipWeekends) {
                cell.setCellValue("");
                continue;
            }

            XSSFColor colorNext = (XSSFColor) headerRow.getCell(colIndex + 2).getCellStyle().getFillForegroundColorColor();
            String rgbHexNext = "#FFFFFF";
            if (colorNext != null) {
                rgbHexNext = "#" + colorNext.getARGBHex().substring(2, 8).toUpperCase();
            }
            if (headerRow.getCell(colIndex + 2) != null && rgbHexNext.equals("#FFFF00")) {
                headerRow.getCell(colIndex + 2).setCellValue("");
            }

            cell.setCellValue("");
            CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(cell.getCellStyle());
            applyColor(newStyle, cell, row);
            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(newStyle);
        }
    }

    // Subclasses set the fill color (and any extra side effects) for each processed cell
    protected abstract void applyColor(CellStyle style, Cell cell, Row row);

    // Whether weekend cells (yellow/purple) are skipped; true for all types except medical and demisie
    protected boolean skipsWeekend() { return true; }

    // Hook: runs once before the main range (e.g. demisie colors the name cell here)
    protected void beforeRange(Row row, Row headerRow) { }

    // Hook: runs after the main range (e.g. demisie extends coloring to end of month)
    protected void afterRange(Row row, Row headerRow, int daysInMonth) { }

    // Hook: writes to the summary columns at the end of the row
    protected void updateSummary(Row row, Row headerRow, int daysInMonth) { }

    // Shared utility used by concediu and absenta to compute the summary cell value,
    // subtracting weekend days that fall within the holiday period
    protected static String getAwayValue(Row headerRow, Row row, int firstDay, int lastDay, Cell awayCell) {
        String awayCellValue;
        if (awayCell.getCellType() == CellType.STRING) {
            awayCellValue = awayCell.getStringCellValue().trim();
            if (awayCellValue.isEmpty()) {
                awayCellValue = Integer.toString(lastDay - firstDay + 1);
            } else {
                try {
                    int current = Integer.parseInt(awayCellValue);
                    awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
                } catch (NumberFormatException e) {
                    awayCellValue = awayCellValue + " + " + (lastDay - firstDay + 1);
                }
            }
        } else if (awayCell.getCellType() == CellType.NUMERIC) {
            int current = (int) awayCell.getNumericCellValue();
            awayCellValue = Integer.toString(current + (lastDay - firstDay + 1));
        } else {
            awayCellValue = Integer.toString(lastDay - firstDay + 1);
        }

        for (int i = firstDay; i <= lastDay; i++) {
            if (i < 1 || i > 31) break;
            int colIndex = i + 4;
            if (colIndex >= row.getLastCellNum()) break;
            Cell cell = row.getCell(colIndex);
            if (cell == null) continue;

            XSSFColor color = (XSSFColor) headerRow.getCell(colIndex).getCellStyle().getFillForegroundColorColor();
            String rgbHex = "#FFFFFF";
            if (color != null) {
                rgbHex = "#" + color.getARGBHex().substring(2, 8).toUpperCase();
            }
            if (headerRow.getCell(colIndex) != null && (rgbHex.equals("#FFFF00") || rgbHex.equals("#CC00FF"))) {
                try {
                    int current = Integer.parseInt(awayCellValue);
                    awayCellValue = Integer.toString(current - 1);
                } catch (NumberFormatException e) {
                    // cannot subtract, leave as-is
                }
            }
        }
        return awayCellValue;
    }


    public void setTotal(int total) {
        this.totalDays = total;
    }
}
