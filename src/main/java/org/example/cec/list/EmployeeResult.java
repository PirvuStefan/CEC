package org.example.cec.list;

public record EmployeeResult(int key, Person person, String employmentDateStr, String valabilityStr,
                             String holidayPeriods, int holidayUsed, int holidayLeftCurrentYear,
                             int holidayLeftLastYears) {

    public int getHolidayLeftTotal() {
        return holidayLeftCurrentYear + holidayLeftLastYears;
    }
}