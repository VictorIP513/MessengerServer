package messenger.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LocalizationProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationProperties.class);
    private static final String LOCALIZATION_PROPERTIES_FILE = "/localization/localization.properties";
    private static final Charset LOCALIZATION_PROPERTIES_CHARSET = StandardCharsets.UTF_8;

    private static Properties properties;

    private LocalizationProperties() {

    }


    public static synchronized String getProperty(String key) {
        if (properties == null) {
            initProperties();
        }
        return properties.getProperty(key);
    }

    private static void initProperties() {
        properties = new Properties();
        try (InputStream inputStream = LocalizationProperties.class.getResourceAsStream(LOCALIZATION_PROPERTIES_FILE);
             InputStreamReader streamReader = new InputStreamReader(inputStream, LOCALIZATION_PROPERTIES_CHARSET)) {
            properties.load(streamReader);
        } catch (IOException e) {
            LOGGER.error("Error loading properties file " + LOCALIZATION_PROPERTIES_FILE, e);
        }
    }
}
