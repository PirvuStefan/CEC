package org.example.cec;

public class Holiday {
    private final int firstDay;
    private final int lastDay;
    // prima zi de concediu si ultima zi de concediu
    private String reason; // condediu, maternitate, medical, absentaMotivata, absentaNemotivata
    private String name; // numele angajatului;
    private String magazin; // magazinul unde lucreaza angajatul
    Holiday(int firstDay, int lastDay, String reason, String name, String magazin){
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.reason = reason;
        this.name = name;
        this.magazin = magazin;
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
    public String getMagazin() {return magazin; }

}
