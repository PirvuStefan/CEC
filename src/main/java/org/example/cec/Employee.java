package org.example.cec;

public class Employee {
    String name; // numele angajatului
    int numberOfShifts; // numarul de ture care trebuie sa fie acoperite in luna respectiva
    int[] shifts = new int[20];   // 0 gol, 1 weekend, 2 sarbatoare

    Employee(String name, int numberOfShifts) {
        this.name = name;
        this.numberOfShifts = numberOfShifts;

    }

}
