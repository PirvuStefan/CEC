package org.example.cec.list;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Person {

    private final String name;
    private final int salary;
    private final LocalDate employmentDate;
    private final String CNP;
    private final String job;
    private final String phoneNumber;
    private final String CI;
    private final String gestiune;
    private final String placeOfWork;
    private final String domicile;
    private final LocalDate valability;

    public Person(PersonBuilder builder) {
        this.name = builder.name;
        this.salary = builder.salary;
        this.employmentDate = builder.employmentDate;
        this.CNP = builder.CNP;
        this.job = builder.job;
        this.CI = builder.CI;
        this.valability = builder.valability;
        this.phoneNumber = builder.phoneNumber;
        this.gestiune = builder.gestiune;
        this.placeOfWork = builder.placeOfWork;
        this.domicile = builder.domicile;
    }


    public String getName() {
        return name;
    }

    public int getSalary() {
        return salary;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public LocalDate getValability(){return valability;}

    public String getCNP() {
        return CNP;
    }

    public String getJob() {
        return job;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCI(){
        return CI;
    }

    public String getGestiune() {
        return gestiune;
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public String getDomicile() {
        return domicile;
    }

    public static class PersonBuilder {
        private String name;
        private int salary;
        private LocalDate employmentDate;
        private String CNP;
        private String job;
        private String phoneNumber;
        private String CI;
        private String gestiune;
        private String placeOfWork;
        private String domicile;
        private LocalDate valability;

        public PersonBuilder() {
        }

        public PersonBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder setName(String name, boolean ok) {
            this.name = name;

            if (ok) {
                this.salary = 4050;
                this.employmentDate = LocalDate.now();
                this.CNP = "1990101123456";
                this.job = "Mock Job Title";
                this.phoneNumber = "0700123456";
                this.CI = "AB123456";
                this.gestiune = "Mock Gestiune";
                this.placeOfWork = "Mock Location";
                this.domicile = "Mock Domicile Address";
                this.valability = LocalDate.now().plusYears(5);
            }

            return this;
        }


        public PersonBuilder setSalary(String salary) {
            try {
                this.salary = Integer.parseInt(salary);
            } catch (NumberFormatException e) {
                this.salary = 0;
            }
            return this;
        }

        public PersonBuilder setEmploymentDate(LocalDate employmentDate) {
            this.employmentDate = employmentDate;
            return this;
        }

        public PersonBuilder setEmploymentDate(String dateString) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                this.employmentDate = LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd", e);
            }
            return this;
        }

        public PersonBuilder setValability(LocalDate valabilityDate){
            this.valability = valabilityDate;
            return this;
        }

        public PersonBuilder setValability(String dateString) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                this.valability = LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd", e);
            }
            return this;
        }

        public PersonBuilder setCNP(String CNP) {
            this.CNP = CNP;
            return this;
        }

        public PersonBuilder setJob(String job) {
            this.job = job;
            return this;
        }

        public PersonBuilder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public PersonBuilder setGestiune(String gestiune) {
            this.gestiune = gestiune;
            return this;
        }


        public PersonBuilder setPlaceOfWork(String placeOfWork) {
            this.placeOfWork = placeOfWork;
            return this;
        }

        public PersonBuilder setDomicile(String domicile) {
            this.domicile = domicile;
            return this;
        }

        public PersonBuilder setCI(String CI){
            this.CI = CI;
            return this;
        }

        public Person build() {
            return new Person(this);
        }


    }
}
