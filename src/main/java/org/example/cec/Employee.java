package org.example.cec;

public class Employee {
    String name; // numele angajatului
    int numberOfShifts; // numarul de ture care trebuie sa fie acoperite in luna respectiva
    boolean hasWorkedSaturday;
    WeekendShift shift;   //

    Employee(String name, int numberOfShifts, WeekendShift shift) {
        this.name = name;
        this.numberOfShifts = numberOfShifts;
        this.hasWorkedSaturday = false; // presupunem ca nu a lucrat sambata in luna precedenta
        this.shift = shift;
    }

    public void hasWorked() {
        this.hasWorkedSaturday = true;
    }

}

// de exemplu ma gandesc, o luna poate sa :
// - inceapa cu ambele zi de weekend ( si sambata si duminica)
// - inceapa cu doar o zi de weekend ( incepe cu duminica deoarece sambata a fost ultima zi din luna precedenta)
// - incepe cu nici o zi de weekend
// - termine cu ambele zi de weekend ( si sambata si duminica)
// - termine cu doar o zi de weekend ( termina cu sambata deoarece duminica este prima zi din luna urmatoare)
// - termine cu nici o zi de weekend

// ne intereseaza astfel doar daca cineva a lucrat in luna precendenta, sambata respectiva, in cazul in care luna curenta incepe cu o zi de duminica
