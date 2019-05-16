package messenger.dao;

import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SuppressWarnings("unused")
@Component
public class UserDao {

    private static final String LOGIN_COLUMN_NAME = "login";
    private static final String EMAIL_COLUMN_NAME = "email";
    private static final String UUID_COLUMN_NAME = "uuid";

    @Autowired
    private DatabaseUtils databaseUtils;

    public boolean isExistLogin(String login) {
        return databaseUtils.checkExistenceObject(User.class, LOGIN_COLUMN_NAME, login);
    }

    public boolean isExistEmail(String email) {
        return databaseUtils.checkExistenceObject(User.class, EMAIL_COLUMN_NAME, email);
    }

    public void saveUserToDatabase(User user) {
        databaseUtils.saveObject(user);
    }

    public User getUserFromUUID(UUID uuid) {
        return databaseUtils.getUniqueObjectByField(User.class, UUID_COLUMN_NAME, uuid);
    }

    public User getUserByLoginAndPassword(String login, String password) {
        User user = databaseUtils.getUniqueObjectByField(User.class, LOGIN_COLUMN_NAME, login);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserByLogin(String login) {
        return databaseUtils.getUniqueObjectByField(User.class, LOGIN_COLUMN_NAME, login);
    }
}
