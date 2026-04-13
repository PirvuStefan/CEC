package org.example.cec.list.add;

import org.example.cec.list.Person;

import java.io.File;


public class AddEmployee {


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
