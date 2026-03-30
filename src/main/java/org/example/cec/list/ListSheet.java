package org.example.cec.list;

public enum ListSheet {
    EMPLOYEE_SEARCH("3");

    private final String value;

    ListSheet(String value) {
        this.value = value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }
}
