package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    public static Properties loadProperties(String env) {
        Properties prop = new Properties();
        if (env == null || env.trim().isEmpty()) {
            env = "staging";
        }

        String path = System.getProperty("user.dir") + "/src/test/resources/config/" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(path)) {
            prop.load(fis);
        } catch (IOException e) {
            // Fallback to classpath if running inside jar or IDE
            try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream("config/" + env + ".properties")) {
                if (is != null) {
                    prop.load(is);
                } else {
                    throw new RuntimeException("Config file not found for env: " + env, e);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load properties for env: " + env, ex);
            }
        }
        return prop;
    }
}

