package org.example.cec.list.add.config;

import java.awt.font.TextHitInfo;
import java.io.IOException;
import java.time.LocalDate;

public class JsonConfig {

    private static JsonConfig instance ;
    private String salary;
    private LocalDate lastUpdate;

    private JsonConfig() {
        System.out.println("Create Config");
        JsonFileReader jsonFileReader = new JsonFileReader();
        this.salary = jsonFileReader.getSalary();
        this.lastUpdate = jsonFileReader.getDate() != null ? LocalDate.parse(jsonFileReader.getDate()) : LocalDate.now();
    }

    public static synchronized JsonConfig getInstance() throws IOException {
        if (instance == null) {
            instance = new JsonConfig();
        }
        return instance;
    }

    public String getSalary(){
        return salary;
    }

    public LocalDate getLastUpdate(){
        return lastUpdate;
    }
}
