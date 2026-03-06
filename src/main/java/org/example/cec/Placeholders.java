package org.example.cec;


public enum Placeholders {
    WORKING_OFFSET("5"),
    MEDICAL_OFFSET("6"),
    HOLIDAY_OFFSET("7"),
    WEEKEND_OFFSET("9"),
    SARBATORI_OFFSET("10"),
    ABSENTEE_OFFSET("15");

    private final String value;

    Placeholders(String value) {
        this.value = value;
    }

    // Convert to int directly within the enum
    public int asInt() {
        return Integer.parseInt(value);
    }
}