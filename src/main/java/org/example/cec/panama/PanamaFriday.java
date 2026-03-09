package org.example.cec.panama;

import org.apache.poi.ss.usermodel.Row;

import static org.example.cec.HelloApplication.daysInMonth;

public class PanamaFriday extends Panama {


    public PanamaFriday(int lastDay) {
        this.lastDay = lastDay;
    }



    // Tuesday, Thursday, Friday
    // 3 days a week

    public void setWeekShift( Row row) {
        // last day is always Sunday, so we start from Sunday and go backwards to see if we can parse a full week of Friday, Thursday, Tuesday
        lastDay = lastDay - 2;
        if(lastDay> 0 && lastDay <= daysInMonth) {
            row.getCell(lastDay).setCellValue(11);
        }
        lastDay--;
        if(lastDay > 0 && lastDay <= daysInMonth) {
            row.getCell(lastDay).setCellValue(11);
        }
        lastDay = lastDay - 2;
        if(lastDay > 0 && lastDay <= daysInMonth) {
            row.getCell(lastDay).setCellValue(11);
        }

    }

    public void print() {
        System.out.println("Panama Friday: " + lastDay);

    }
}
