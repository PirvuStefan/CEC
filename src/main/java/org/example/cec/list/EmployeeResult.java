package org.example.cec.list;

public class EmployeeResult {

    private final int key;
    private final Person person;
    private final String employmentDateStr;
    private final String valabilityStr;
    private final String holidayPeriods;
    private final int holidayUsed;
    private final int holidayLeftCurrentYear;
    private final int holidayLeftLastYears;

    public EmployeeResult(int key, Person person, String employmentDateStr, String valabilityStr,
                          String holidayPeriods, int holidayUsed, int holidayLeftCurrentYear, int holidayLeftLastYears) {
        this.key = key;
        this.person = person;
        this.employmentDateStr = employmentDateStr;
        this.valabilityStr = valabilityStr;
        this.holidayPeriods = holidayPeriods;
        this.holidayUsed = holidayUsed;
        this.holidayLeftCurrentYear = holidayLeftCurrentYear;
        this.holidayLeftLastYears = holidayLeftLastYears;
    }

    public int getKey() { return key; }
    public Person getPerson() { return person; }
    public String getEmploymentDateStr() { return employmentDateStr; }
    public String getValabilityStr() { return valabilityStr; }
    public String getHolidayPeriods() { return holidayPeriods; }
    public int getHolidayUsed() { return holidayUsed; }
    public int getHolidayLeftCurrentYear() { return holidayLeftCurrentYear; }
    public int getHolidayLeftLastYears() { return holidayLeftLastYears; }
    public int getHolidayLeftTotal() { return holidayLeftCurrentYear + holidayLeftLastYears; }
}