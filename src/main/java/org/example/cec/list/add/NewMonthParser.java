package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.*;
import org.example.cec.list.MonthsPlaceholders;

public class NewMonthParser {

    Row row;
    MonthsPlaceholders month ;

    NewMonthParser(Row row, MonthsPlaceholders month){
        this.row = row;
        this.month = month;
    }

    public void start(){
        for(int i = 0;  i < 31; i++){

            Cell cell = row.getCell(i);

            if( cell == null ) continue;
            CellStyle newStyle = row.getSheet().getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(cell.getCellStyle());
            newStyle.setFillForegroundColor((IndexedColors.YELLOW.getIndex()));
            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(newStyle);


        }

        Cell firstCell = row.getCell(0);
        if(firstCell == null) firstCell = row.createCell(0);
        firstCell.setCellValue(month.name());
    }
}
