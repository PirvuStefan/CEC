package org.example.cec.list.add.config;

import java.io.IOException;

public class SalaryConfig {

    private static SalaryConfig instance ;
    private String salary;

    private SalaryConfig() {
        System.out.println("Create Config");
        try {
            JsonFileReader jsonFileReader = new JsonFileReader();
            this.salary = jsonFileReader.getSalary();
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
            this.salary = "0";
        }
    }

    public static synchronized SalaryConfig getInstance() throws IOException {
        if (instance == null) {
            instance = new SalaryConfig();
        }
        return instance;
    }

    public String getSalary(){
        return salary;
    }
}
