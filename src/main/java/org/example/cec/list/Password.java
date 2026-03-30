// java
package org.example.cec.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Password {
    private final String password;

    public Password() {
        // Option A: use the class's resource lookup with leading '/'
        InputStream in = Password.class.getResourceAsStream("/password");
        // Option B (alternate): use the class loader without leading slash
        // InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("password.txt");

        if (in == null) {
            throw new IllegalStateException("Resource `/password.txt` not found on classpath. Place it in `src/main/resources/password.txt`.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            this.password = br.readLine(); // adapt: read whole file if needed
        } catch (IOException e) {
            throw new RuntimeException("Failed to read `/password.txt` from classpath", e);
        }
    }



    public String getValue() {
        return password;
    }
}
