package org.example.cec;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import static org.example.cec.ui.MainScene.daysInMonth;
import static org.example.cec.ParseWorkingHours.getStartDay;
import static org.example.cec.ParseWorkingHours.checkColor;

public class ParseIndividualHours {

    static int startDay ;

    static void Parse(Row row){

        if(!checkParse(row)) return;

        for(int i = startDay; i <= daysInMonth;i++){
            Cell cell = row.getCell(i);
            if(!checkColor(cell)) continue;
            if(cell.getCellType() == CellType.STRING) cell.setCellValue("8");
            else cell.setCellValue(8);
        }

    }




    static boolean checkParse(Row row) {
        int count = 0;
        startDay = getStartDay(row);


        for (int i = startDay; i < daysInMonth; i++) {
            Cell cell = row.getCell(i);
            if (!checkColor(cell)) continue;
            if(checkCell(cell)) count++;

        }

        return (count>0);




    }

    static boolean checkCell(Cell cell) {
        // we check if the cell is not empty (if we do have a number in it, 8 lets say

        if (cell == null) return false;


        String cellValue = "";
        if (cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cellValue = String.valueOf((int) cell.getNumericCellValue());
        }

        return !cellValue.isEmpty();
    }
}

