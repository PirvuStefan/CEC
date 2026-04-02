package org.example.cec.list;

public enum ListSheet {
    EMPLOYEE_SEARCH("3"),
    EMPLOYEE_LIST("0");

    private final String value;

    ListSheet(String value) {
        this.value = value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }
}
