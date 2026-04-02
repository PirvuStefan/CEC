package org.example.cec.list;

public enum MonthsPlaceholders {

    Ianuarie("1"),
    Februarie("2"),
    Martie("3"),
    Aprilie("4"),
    Mai("5"),
    Iunie("6"),
    Iulie("7"),
    August("8"),
    Septembrie("9"),
    Octombrie("10"),
    Noiembrie("11"),
    Decembrie("12");

    private final String value;

    MonthsPlaceholders(String value){ this.value = value;}

    public int asInt() {
        return Integer.parseInt(value);
    }

    public static String asString(String val) {
        for (MonthsPlaceholders month : values()) {
            if (month.value.equals(val)) {
                return month.name();
            }
        }
        throw new IllegalArgumentException("Invalid month number: " + val);
    }

    public static String asString(int val) {
        return asString(String.valueOf(val));
    }

}
