package org.example.cec.list;

import java.util.Date;

public class Person {

    private final String name;
    private final int salary;
    private final Date employmentDate;
    private final String CNP;
    private final String job;
    private final String phoneNumber;
    private final String gestiune;
    private final boolean newMonth;
    private final String placeOfWork;
    private final String domicile;

    private Person(PersonBuilder builder) {
        this.name = builder.name;
        this.salary = builder.salary;
        this.employmentDate = builder.employmentDate;
        this.CNP = builder.CNP;
        this.job = builder.job;
        this.phoneNumber = builder.phoneNumber;
        this.gestiune = builder.gestiune;
        this.newMonth = builder.newMonth;
        this.placeOfWork = builder.placeOfWork;
        this.domicile = builder.domicile;
    }


    public String getName() {
        return name;
    }

    public int getSalary() {
        return salary;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public String getCNP() {
        return CNP;
    }

    public String getJob() {
        return job;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGestiune() {
        return gestiune;
    }

    public boolean isNewMonth() {
        return newMonth;
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public String getDomicile() {
        return domicile;
    }

    protected static class PersonBuilder {
        private String name;
        private int salary;
        private Date employmentDate;
        private String CNP;
        private String job;
        private String phoneNumber;
        private String gestiune;
        private boolean newMonth;
        private String placeOfWork;
        private String domicile;

        public PersonBuilder() {
        }

        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder salary(int salary) {
            this.salary = salary;
            return this;
        }

        public PersonBuilder employmentDate(Date employmentDate) {
            this.employmentDate = employmentDate;
            return this;
        }

        public PersonBuilder CNP(String CNP) {
            this.CNP = CNP;
            return this;
        }

        public PersonBuilder job(String job) {
            this.job = job;
            return this;
        }

        public PersonBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public PersonBuilder gestiune(String gestiune) {
            this.gestiune = gestiune;
            return this;
        }

        public PersonBuilder newMonth(boolean newMonth) {
            this.newMonth = newMonth;
            return this;
        }

        public PersonBuilder placeOfWork(String placeOfWork) {
            this.placeOfWork = placeOfWork;
            return this;
        }

        public PersonBuilder domicile(String domicile) {
            this.domicile = domicile;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
