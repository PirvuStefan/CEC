package org.example.cec.list.add;

import org.example.cec.list.Person;

public class AddEmployee {

    Person person ;
    boolean isNewMonth;

    public AddEmployee(Person person, boolean isNewMonth) {
        this.person = person;
        this.isNewMonth = isNewMonth;


    }

    public void start(){
        AddToList addToList = new AddToList(person, isNewMonth);
        addToList.start();
    }


}
