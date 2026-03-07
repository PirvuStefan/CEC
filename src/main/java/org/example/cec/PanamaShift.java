package org.example.cec;

import org.apache.poi.ss.usermodel.Row;
import org.example.cec.panama.Panama;

import java.util.ArrayList;

import static org.example.cec.WeekendModify.checkColor;
import static org.example.cec.WeekendShift.getValueint;

public class PanamaShift {

    static String name; // numele angajatului
    static int size;
    static int sarbatoriSize;
    static int[] pos;
    static int[] sarbatoare;
    ArrayList<Panama> panamaList = new ArrayList<>();

    public void initialiseDays(int size){
        PanamaShift.size = size;
    }


    public void initialiseSize(Row row){
        int count = 0;
        int sarbatoriCount = 0;
        int position = 0;
        pos = new int[32];
        sarbatoare = new int[15];

        while (row.getCell(count + 2) != null) {

            if(!checkColor(row.getCell(count + 2))){
                System.out.println("NU E ALB " + getValueint(row, count + 2) ) ;
                sarbatoare[sarbatoriCount++] = getValueint(row, count + 2);
            }
            else pos[position++] = getValueint(row, count + 2);
            count++;
            if( count > 15) break;
        }
    }


}
