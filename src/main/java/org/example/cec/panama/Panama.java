package org.example.cec.panama;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public abstract class Panama {

    int lastDay; // this is the Sunday of that specific week

    public abstract void setWeekShift(Row row);
    public abstract void print();
    public int getLastDay(){
        return lastDay;
    }

    protected void setCellValue(Cell cell){
        if(cell == null) return;
        if(cell.getCellType().equals(CellType.NUMERIC)){
            cell.setCellValue(11);
        }
        else  cell.setCellValue("11");
    }


}
