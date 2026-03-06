package org.example.cec;

public enum Placeholders {

    WORKING_OFFSET("5"),
    MEDICAL_OFFSET("6"),
    HOLIDAY_OFFSET("7"),
    ABSENTEE_OFFSET("15");

    private final String value;

    Placeholders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
