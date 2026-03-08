package org.example.cec.panama;

import org.apache.poi.ss.usermodel.Row;

public abstract class Panama {

    int lastDay; // this is the Sunday of that specific week

    public abstract void setWeekShift(Row row);


}
