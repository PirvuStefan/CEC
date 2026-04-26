package org.example.cec;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.example.cec.ui.MainScene.daysInMonth;
import static org.example.cec.WeekendShift.whatDay;
import static org.example.cec.Placeholders.*;

public class WeekendModify {

    private File mainSheet, weekendSheet;

    public WeekendModify(File mainSheet, File weekendSheet) {
        this.mainSheet = mainSheet;
        this.weekendSheet = weekendSheet;
    }

    public File launch(){

        Map< String , List<Employee>> weekendEmployees;
        System.out.println("Modifying main sheet with weekend shifts...");
        WeekendShift test = new WeekendShift();
        test.initialiseSize(weekendSheet); // to set the size of the weekend shift ( static variable);
        System.out.println("Weekend size: " + WeekendShift.size);
        WeekendInitialize initializer = new WeekendInitialize(weekendSheet);
        weekendEmployees = initializer.initialiseWeekendList();
        if( weekendEmployees.isEmpty()){
            System.out.println("Eroare la initializarea listei de angajati pentru weekend!");
        }
        else System.out.println("Lista de angajati pentru weekend a fost initializata cu succes!");

        //ParseWorkingHours.initializeSheet(mainSheet, daysInMonth);

        for( String magazin : weekendEmployees.keySet()){




            System.out.println("Magazin: " + magazin);
            System.out.print("----------------------\n");
            List < Employee > employees = weekendEmployees.get(magazin);
            int[] numberOfShifts = new int[employees.size()];
            for( int i = 0; i < employees.size(); i++) numberOfShifts[i] = employees.get(i).numberOfShifts;

            WeekendGenerate generator = new WeekendGenerate(numberOfShifts, WeekendShift.pos, employees, weekendSheet);

            int[][] y = generator.y;
            int[][] x = generator.x;





            for(int i = 0 ; i < x.length; i++){
                System.out.println("Employee: " + employees.get(i).name + " , number of shifts: " + employees.get(i).numberOfShifts );
                for(int j = 0; j < x[i].length; j++){
                    System.out.print(x[i][j] + " ");
                }
                System.out.print("\n");
            }




            for( int i = 0; i < employees.size(); i++){
                mainSheet = WeekendModifyEmployee.launch(mainSheet, employees.get(i).name.toUpperCase(), x[i], WeekendShift.pos, y[i], WeekendShift.sarbatoare);
            }

        }


        System.out.println("Main sheet updated with weekend shifts successfully!");
        return mainSheet;
    }




    static boolean checkColor(Cell cell){
        String s;
        if( cell == null ) return true;
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
        String rgbHex = "#FFFFFF"; // default white color
        if(color != null){
            String hexColor = color.getARGBHex();
            rgbHex = hexColor.substring(2, 8); // remove alpha channel
            rgbHex = "#" + rgbHex.toUpperCase();
        }
        return ( rgbHex.equals("#FFFFFF") || rgbHex.equals("#002060") ); // white or bluemarin
    }









}
