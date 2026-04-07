package org.example.cec.list.add.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;

public class JsonFileReader {

    protected String getSalary() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File externalConfig = new File("arhiva/config.json");
            JsonNode jsonNode = objectMapper.readTree(externalConfig);
            return jsonNode.get("salary").asText();
        } catch (Exception e) {
            InputStream in = getClass().getResourceAsStream("/config.json");
            if (in == null) {
                throw new IOException("config.json not found in resources, and loading from arhiva/config.json failed", e);
            }
            JsonNode jsonNode = objectMapper.readTree(in);
            return jsonNode.get("salary").asText();
        }
    }

}
