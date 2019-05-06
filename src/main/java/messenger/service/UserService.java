package messenger.service;

import messenger.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean isExistLogin(String login) {
        return userDao.isExistLogin(login);
    }

    public boolean isExistEmail(String email) {
        return userDao.isExistEmail(email);
    }
}
