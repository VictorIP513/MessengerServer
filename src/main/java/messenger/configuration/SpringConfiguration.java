package messenger.configuration;

import com.google.gson.Gson;
import messenger.utils.DatabaseUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class SpringConfiguration {

    private static final String HIBERNATE_CONFIGURATION_FILE = "configuration/hibernate.cfg.xml";

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
        return new Gson();
    }
}
