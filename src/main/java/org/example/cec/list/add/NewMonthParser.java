package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.list.MonthsPlaceholders;

import java.io.FileOutputStream;
import java.io.IOException;

public class NewMonthParser {

    Row row;
    MonthsPlaceholders month ;

    NewMonthParser(Row row, MonthsPlaceholders month){
        this.row = row;
        this.month = month;
    }

    public void start() {
        Workbook workbook = row.getSheet().getWorkbook();

        CellStyle highlightStyle = workbook.createCellStyle();

        highlightStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        System.out.println(row.getRowNum());

        for (int i = 0; i < 31; i++) {
            Cell cell = row.getCell(i);

            if (cell == null) {
                cell = row.createCell(i);
            }


            cell.setCellStyle(highlightStyle);
        }


        Cell firstCell = row.getCell(0);
        if (firstCell == null) {
            firstCell = row.createCell(0);
        }
        firstCell.setCellValue(month.name() + " " + month.getCurrentYear());


    }


}
