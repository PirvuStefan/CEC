package org.example.cec.list.add;

import org.example.cec.list.Person;

import java.io.File;


public class AddEmployee {

    public final double DAY_ADD = 0.055; // daily constant for each worked day, calculated as 20/365

    Person person ;
    boolean isNewMonth;
    File file;

    public AddEmployee(Person person, boolean isNewMonth, File file) {
        this.person = person;
        this.isNewMonth = isNewMonth;
        this.file = file;


    }

    public void start(){
        AddToList addToList = new AddToList(person, isNewMonth);
        addToList.start();
        AddToMain addToMain = new AddToMain(person,file);
        addToMain.start();
    }


}
