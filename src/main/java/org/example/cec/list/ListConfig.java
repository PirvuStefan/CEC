package org.example.cec.list;

import org.example.cec.ui.SceneController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ListConfig {

    private static ListConfig instance;
    private final Path outputDir = Path.of("arhiva/list");
    protected Password password;
    private File file;

    ListConfig() {
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        file = search();
        password = new Password();

    }

    private File search(){
        File[] files = outputDir.toFile().listFiles();
        if(files == null) return null;

        for(File file : files){
            if(file.getName().contains("aux")) continue;
            return file;
        } // return the file that does not contain aux since we might need to create additional copy of the main list for safe deletion

        return null;
    }

    public static synchronized ListConfig getInstance() {
        if (instance == null) {
            instance = new ListConfig();
        }
        return instance;
    }

    protected Path getOutputDir() {
        return outputDir;
    }

    protected File getFile() {
        return file;
    }
}
