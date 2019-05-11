package messenger.dao;

import messenger.model.EmailStatus;
import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EmailStatusDao {

    private static final String USER_COLUMN_NAME = "user";

    @Autowired
    private DatabaseUtils databaseUtils;

    public void saveEmailStatusToDatabase(EmailStatus emailStatus) {
        databaseUtils.saveObject(emailStatus);
    }

    public void activateUser(User user) {
        EmailStatus emailStatus = databaseUtils.getUniqueObjectByField(EmailStatus.class, USER_COLUMN_NAME, user);
        emailStatus.setConfirmStatus(true);
        databaseUtils.updateObject(emailStatus);
    }

    public boolean isActivateUser(User user) {
        EmailStatus emailStatus = databaseUtils.getUniqueObjectByField(EmailStatus.class, USER_COLUMN_NAME, user);
        return emailStatus.isConfirmStatus();
    }
}
