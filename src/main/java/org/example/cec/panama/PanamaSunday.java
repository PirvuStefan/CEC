package org.example.cec.panama;

import org.apache.poi.ss.usermodel.Row;

public class PanamaSunday extends Panama {

    public void setWeekShift(int lastDay, Row row) {
         // lastDay is always Sunday, so we start from Sunday and go backwards to see if we can parse a full week of Sunday, Saturday, Wednesday, Monday
         if(lastDay-- > 0) {
             row.getCell(lastDay).setCellValue(11);
         }
         if(lastDay > 0) {
             row.getCell(lastDay).setCellValue(11);
         }
         lastDay = lastDay - 3;
         if(lastDay > 0) {
             row.getCell(lastDay).setCellValue(11);
         }
         lastDay = lastDay - 2;
         if(lastDay > 0) {
             row.getCell(lastDay).setCellValue(11);
         }
     }

    // Monday, Wednesday, Saturday, Sunday
    // 4 days a week
}
