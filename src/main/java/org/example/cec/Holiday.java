package org.example.cec;

public class Holiday {
    private int firstDay;
    private int lastDay;
    // prima zi de concediu si ultima zi de concediu
    private String reason; // condediu, maternitate, medical, absentaMotivata, absentaNemotivata
    private String name; // numele angajatului;
    Holiday(int firstDay, int lastDay, String reason, String name){
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.reason = reason;
        this.name = name;
    }
    Holiday(int firstDay, int lastDay){
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }
    public int getFirstDay() {
        return firstDay;
    }
    public int getLastDay() {
        return lastDay;
    }
    public String getReason() {return reason; }
    public String getName() {return name; }


}
