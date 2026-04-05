package org.example.cec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public interface CellValue {

    default int getValueint(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return extractCell(cell);

    }

    default int getValueInt(Cell cell){
        return extractCell(cell);
    }

    private int extractCell(Cell cell) {
        if(cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0; // sau altă valoare implicită
            }
        }

        return 0; // sau altă valoare implicită
    }

    default void setValue(Row row, int cellIndex, String value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        if(cell.getCellType() == CellType.STRING) {
            cell.setCellValue(value);
            return;
        }
        else if(cell.getCellType() == CellType.NUMERIC) {
            try {
                cell.setCellValue(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                cell.setCellValue(value); // dacă nu e un numar, seteaza ca text
            }
        }
    }

     default void setValue(Row row, int cellIndex, int value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        if(cell.getCellType() == CellType.NUMERIC) {
            cell.setCellValue(value);
            return;
        }
        else if(cell.getCellType() == CellType.STRING) {
            cell.setCellValue(Integer.toString(value));
        }
    }

    default void setValue(Cell cell, int value) {
        if (cell == null) {
            return;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            cell.setCellValue(value);
        } else if (cell.getCellType() == CellType.STRING) {
            cell.setCellValue(Integer.toString(value));
        }
    }

    default void setValue(Cell cell, String value) {
        if (cell == null) {
            return;
        }
        if (cell.getCellType() == CellType.STRING) {
            cell.setCellValue(value);
        } else if (cell.getCellType() == CellType.NUMERIC) {
            try {
                cell.setCellValue(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                cell.setCellValue(value); // dacă nu e un numar, seteaza ca text
            }
        }
    }
}
