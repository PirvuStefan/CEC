package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import static org.example.cec.ui.MainScene.daysInMonth;

public class ParseIndividualHours {

    private final int startDay;
    private final Row row;

    public ParseIndividualHours(Row row) {
        this.row = row;
        this.startDay = getStartDay(row);

    }

    public void parse() {
        if (!checkParse()) return;

        for (int i = startDay; i <= daysInMonth; i++) {
            int col = i + Placeholders.DAY_OFFSET.asInt();
            Cell cell = row.getCell(col);

            if (cell == null) {
                cell = row.createCell(col, CellType.NUMERIC);
            }

            if (checkColor(cell)) continue; // skip blocked/non-editable colors

            if (cell.getCellType() == CellType.STRING) {
                cell.setCellValue("8");
            } else {
                cell.setCellValue(8);
            }
        }
    }

    // true if there is at least one editable empty cell to fill
    public boolean checkParse() {
        for (int i = startDay; i <= daysInMonth; i++) {
            Cell cell = row.getCell(i + Placeholders.DAY_OFFSET.asInt());

            if (checkColor(cell)) continue; // skip blocked cells
            if (!checkCell(cell)) return true; // found editable empty slot
        }
        return false;
    }

    public boolean checkCell(Cell cell) {
        if (cell == null) return false;

        String cellValue = "";
        if (cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cellValue = String.valueOf((int) cell.getNumericCellValue());
        }

        return !cellValue.isEmpty();
    }

    public boolean checkColor(Cell cell) {
        if (cell == null) return false; // allow creation/fill for missing cells

        CellStyle style = cell.getCellStyle();
        if (style == null) return false;

        XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
        if (color == null || color.getARGBHex() == null || color.getARGBHex().length() < 8) {
            return false; // treat unknown/no color as editable
        }

        String rgbHex = "#" + color.getARGBHex().substring(2, 8).toUpperCase();
        return (!rgbHex.equals("#FFFFFF") && !rgbHex.equals("#002060"));
    }

    public int getStartDay(Row row) {
        int start = 1;
        for (int i = 1; i <= daysInMonth; i++) {
            Cell cell = row.getCell(i + Placeholders.DAY_OFFSET.asInt());
            if (cell == null) continue;

            XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
            String rgbHex = "#FFFFFF";
            if (color != null && color.getARGBHex() != null && color.getARGBHex().length() >= 8) {
                rgbHex = "#" + color.getARGBHex().substring(2, 8).toUpperCase();
            }

            if (rgbHex.equals("#002060")) {
                start = i;
            }
        }
        return start;
    }
}
