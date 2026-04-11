package org.example.cec.list.add.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonFileReader {

    Map<String, String> configMap = new HashMap<>();

    JsonFileReader() {
        loadConfig();
    }

    private void loadConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            File externalConfig = new File("arhiva/config.json");
            jsonNode = objectMapper.readTree(externalConfig);
        } catch (Exception e) {
            try {
                InputStream in = getClass().getResourceAsStream("/config.json");
                if (in == null) {
                    throw new IOException("config.json not found in resources, and loading from arhiva/config.json failed", e);
                }
                jsonNode = objectMapper.readTree(in);
            } catch (IOException ex) {
                System.err.println("Could not load config.json: " + ex.getMessage());
            }
        }

        if (jsonNode != null && jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                configMap.put(field.getKey(), field.getValue().asText());
            }
        }
    }

    protected String getSalary() {
        return configMap.get("salary");
    }

    protected String getDate(){
        return configMap.get("lastUpdate");
    }
}
