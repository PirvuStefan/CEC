package org.example.cec.list.update;

import org.apache.poi.ss.usermodel.Row;
import org.example.cec.CellValue;
import org.example.cec.holiday.ConcediuHoliday;

import java.util.ArrayList;

public class HolidayCount implements CellValue {


    String periodDescription;
    ConcediuHoliday holiday;
    ArrayList<ConcediuHoliday> holidays = new ArrayList<>();
    Row row;

    HolidayCount(ConcediuHoliday holiday){
        this.holiday = holiday;

    }


}
