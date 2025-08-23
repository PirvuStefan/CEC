package org.example.cec;

public class WeekendShift {

    private static int size;
    Pair[] days;
    private void initaliseDays(int size){
        this.size = size;
        days = new Pair[size];
        for(int i = 0; i < size; i++){
            days[i] = new Pair(i+1, false);
        }
    }




}
