package org.example.cec.panama;

import org.apache.poi.ss.usermodel.Row;

import static org.example.cec.Placeholders.DAY_OFFSET;
import static org.example.cec.ui.MainScene.daysInMonth;

public class PanamaSunday extends Panama {

    public PanamaSunday(int lastDay) {
        this.lastDay = lastDay;
    }

    public void setWeekShift(Row row) {
         // lastDay is always Sunday, so we start from Sunday and go backwards to see if we can parse a full week of Sunday, Saturday, Wednesday, Monday
         if(lastDay > 0 && lastDay <= daysInMonth) {
             setCellValue(row.getCell(lastDay + DAY_OFFSET.asInt()));
         }
         lastDay--;
         if(lastDay > 0 && lastDay <= daysInMonth) {
             setCellValue(row.getCell(lastDay + DAY_OFFSET.asInt()));
         }
         lastDay = lastDay - 3;
         if(lastDay > 0 && lastDay <= daysInMonth) {
             setCellValue(row.getCell(lastDay + DAY_OFFSET.asInt()));
         }
         lastDay = lastDay - 2;
         if(lastDay > 0 && lastDay <= daysInMonth) {
             setCellValue(row.getCell(lastDay + DAY_OFFSET.asInt()));
         }
     }

    // Monday, Wednesday, Saturday, Sunday
    // 4 days a week


    public void print() {
        System.out.println("Panama Sunday: " + lastDay);
    }
}
