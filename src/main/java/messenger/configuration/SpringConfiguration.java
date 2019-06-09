package messenger.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import messenger.utils.DatabaseUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class SpringConfiguration {

    private static final String HIBERNATE_CONFIGURATION_FILE = "configuration/hibernate.cfg.xml";
    private static final String JSON_DATE_FORMAT = "dd MMM yyyy HH:mm:ss";

    @Bean
    public DatabaseUtils databaseUtils() {
        return new DatabaseUtils();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration()
                .configure(HIBERNATE_CONFIGURATION_FILE)
                .buildSessionFactory();
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(JSON_DATE_FORMAT);
        return gsonBuilder.create();
    }
}
