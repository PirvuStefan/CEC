package org.example.cec;

public class WeekendShift {

    private static int size;
    Pair[] days;
    private void initaliseDays(int size){
        WeekendShift.size = size;
        days = new Pair[size];
        for(int i = 0; i < size; i++){
            days[i].work = false;
        }
    }




}
