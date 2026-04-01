package org.example.cec;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import static org.example.cec.ui.MainScene.daysInMonth;

public class ParseIndividualHours {

    int startDay ;
    Row row;

    ParseIndividualHours(Row row){
        this.row = row;
        this.startDay = getStartDay(row);

    }


     void parse(){

        if(!checkParse()) return;

        System.out.println("Parsing Row " + row.getRowNum() + " for employee: " + row.getCell(2).getStringCellValue());

        for(int i = startDay; i <= daysInMonth;i++){
            Cell cell = row.getCell(i + Placeholders.DAY_OFFSET.asInt());
            if(checkColor(cell)) continue;
            if(cell.getCellType() == CellType.STRING) cell.setCellValue("8");
            else cell.setCellValue(8);
        }

    }




     boolean checkParse() {
        int count = 0;
        startDay = getStartDay(row);


        for (int i = startDay; i < daysInMonth; i++) {
            Cell cell = row.getCell(i + Placeholders.DAY_OFFSET.asInt());
            if(checkCell(cell)) continue;
            if (checkColor(cell)) continue;
            if(checkCell(cell)) count++;

        }

        return (count>0);




    }

    boolean checkCell(Cell cell) {
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

    boolean checkColor( Cell cell ){
        String s;
        if( cell == null ) return true;
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return (!rgbHex.equals("#FFFFFF") && !rgbHex.equals("#002060")); // white or bluemarin
    }

    int getStartDay(Row row){
        int startDay = 1;
        for (int i = 1; i <= daysInMonth; i++) {
            Cell cell = row.getCell(i + Placeholders.DAY_OFFSET.asInt());
            if (cell != null) {
                XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
                String rgbHex = "#FFFFFF"; // default white color
                if (color != null) {
                    String hexColor = color.getARGBHex();
                    rgbHex = hexColor.substring(2, 8); // remove alpha channel
                    rgbHex = "#" + rgbHex.toUpperCase();
                }
                // Bluemarin (navy blue) is usually #000080
                if (rgbHex.equals("#002060") && cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() == 8) {
                    startDay = i;
                    // Add your logic here for bluemarin cells with value 8
                } else if (rgbHex.equals("#002060") && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("8")) {
                    startDay = i;
                } else if (rgbHex.equals("#002060")) startDay = i;
            }
        }
        return startDay;
    }
}

