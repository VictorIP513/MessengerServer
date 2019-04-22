package messenger.service;

import messenger.model.User;
import messenger.repository.RoleRepository;
import messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }
}
