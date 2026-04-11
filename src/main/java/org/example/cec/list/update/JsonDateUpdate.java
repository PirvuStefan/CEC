package org.example.cec.list.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.time.LocalDate;

public class JsonDateUpdate {

    public void start() {
        File configFile = new File("arhiva/config.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            ObjectNode root;
            if (configFile.exists()) {
                root = (ObjectNode) mapper.readTree(configFile);
            } else {
                root = mapper.createObjectNode();
            }

            root.put("lastUpdate", LocalDate.now().toString());
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
