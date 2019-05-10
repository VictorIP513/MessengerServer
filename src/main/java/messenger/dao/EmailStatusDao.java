package messenger.dao;

import messenger.model.EmailStatus;
import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EmailStatusDao {

    private static final String USER_COLUMN_NAME = "user";

    @Autowired
    private DatabaseUtils databaseUtils;

    @Autowired
    private SessionFactory sessionFactory;

    public void saveEmailStatusToDatabase(EmailStatus emailStatus) {
        databaseUtils.saveObject(emailStatus);
    }

    public void activateUser(User user) {
        EmailStatus emailStatus = databaseUtils.getObjectsByField(
                EmailStatus.class, USER_COLUMN_NAME, user).get(0);
        emailStatus.setConfirmStatus(true);
        databaseUtils.updateObject(emailStatus);
    }
}
