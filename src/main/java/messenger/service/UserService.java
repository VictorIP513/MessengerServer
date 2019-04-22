package messenger.service;

import messenger.model.User;

public interface UserService {

    void save(User user);

    User findByLogin(String login);
}
