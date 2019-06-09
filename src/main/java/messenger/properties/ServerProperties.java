package messenger.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ServerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationProperties.class);
    private static final String SERVER_PROPERTIES_FILE = "/server.properties";
    private static final Charset SERVER_PROPERTIES_CHARSET = StandardCharsets.UTF_8;

    private static Properties properties;

    private ServerProperties() {

    }

    public static synchronized String getProperty(String key) {
        if (properties == null) {
            initProperties();
        }
        return properties.getProperty(key);
    }

    private static void initProperties() {
        properties = new Properties();
        try (InputStream inputStream = ServerProperties.class.getResourceAsStream(SERVER_PROPERTIES_FILE);
             InputStreamReader streamReader = new InputStreamReader(inputStream, SERVER_PROPERTIES_CHARSET)) {
            properties.load(streamReader);
        } catch (IOException e) {
            LOGGER.error("Error loading properties file " + SERVER_PROPERTIES_FILE, e);
        }
    }
}
