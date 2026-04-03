package org.example.cec.list.add;

import org.apache.poi.ss.usermodel.Row;
import org.example.cec.list.MonthsPlaceholders;
import org.example.cec.list.Person;

public class AddEmployee {

    Person person ;
    Row row;

    public AddEmployee(Person person, boolean isNewMonth) {
        this.person = person;

        if (isNewMonth) {
            // Placeholder for now, you might need to determine the correct month dynamically.
            MonthsPlaceholders currentMonth = MonthsPlaceholders.getCurrent(); // Adjust as needed
            NewMonthParser newMonthParser = new NewMonthParser(row, currentMonth);
            newMonthParser.start();
        }

        addEmployeeToRow();
    }

    private void addEmployeeToRow() {
        if (row == null || person == null) {
            return;
        }


        row.createCell(0).setCellValue(person.getName());
        row.createCell(1).setCellValue(person.getCNP());
        row.createCell(2).setCellValue(person.getJob());
        row.createCell(3).setCellValue(person.getSalary());

        if (person.getEmploymentDate() != null) {
            row.createCell(4).setCellValue(person.getEmploymentDate().toString()); // Or format Date properly
        }

        row.createCell(5).setCellValue(person.getPhoneNumber());
        row.createCell(6).setCellValue(person.getGestiune());
        row.createCell(7).setCellValue(person.getPlaceOfWork());
        row.createCell(8).setCellValue(person.getDomicile());
    }
}
