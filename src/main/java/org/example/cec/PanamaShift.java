package org.example.cec;

import org.apache.poi.ss.usermodel.Row;
import org.example.cec.panama.Panama;
import org.example.cec.panama.PanamaFriday;
import org.example.cec.panama.PanamaSunday;

import java.io.File;
import java.util.ArrayList;

import static org.example.cec.WeekendModify.checkColor;
import static org.example.cec.WeekendShift.getValueint;

public class PanamaShift extends WeekendShift {

    ArrayList<Panama> panamaList = new ArrayList<>();
    ArrayList<Boolean> sarbatoriList = new ArrayList<>();

    PanamaShift(File path){
       if(PanamaShift.size == -1) initialiseSize(path);
    }


}
