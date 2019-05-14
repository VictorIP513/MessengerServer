package messenger.service;

import messenger.dao.EmailStatusDao;
import messenger.dao.TokenDao;
import messenger.dao.UserDao;
import messenger.model.EmailStatus;
import messenger.model.User;
import messenger.view.LocalizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SuppressWarnings("unused")
@Service
public class UserService {

    private static final String UUID_REGEXP_VALIDATOR =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailStatusDao emailStatusDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private MailSender mailSender;

    public boolean isExistLogin(String login) {
        return userDao.isExistLogin(login);
    }

    public boolean isExistEmail(String email) {
        return userDao.isExistEmail(email);
    }

    public void registerNewUser(User user) {
        user.setUuid(UUID.randomUUID());
        userDao.saveUserToDatabase(user);

        EmailStatus userEmailStatus = new EmailStatus();
        userEmailStatus.setUser(user);
        userEmailStatus.setConfirmStatus(false);
        emailStatusDao.saveEmailStatusToDatabase(userEmailStatus);

        sendEmailConfirmMessage(user);
    }

    public boolean activateUser(String activationCode) {
        if (activationCode.matches(UUID_REGEXP_VALIDATOR)) {
            User user = userDao.getUserFromUUID(UUID.fromString(activationCode));
            if (user != null) {
                emailStatusDao.activateUser(user);
                return true;
            }
        }
        return false;
    }

    public User getUserByLoginAndPassword(String login, String password) {
        return userDao.getUserByLoginAndPassword(login, password);
    }

    public void setNewAuthenticationTokenToUser(String authenticationToken, User user) {
        tokenDao.setNewAuthenticationTokenToUser(authenticationToken, user);
    }

    public boolean isActivateUser(User user) {
        return emailStatusDao.isActivateUser(user);
    }

    public boolean checkCorrectAuthenticationToken(String authenticationToken) {
        return tokenDao.isCorrectAuthenticationToken(authenticationToken);
    }

    private void sendEmailConfirmMessage(User user) {
        String serverIp = ServerProperties.getProperty("server.ip");
        String serverPort = ServerProperties.getProperty("server.port");
        String confirmLink = String.format("http://%s:%s/api/activate/%s",
                serverIp, serverPort, user.getUuid().toString());
        String subject = LocalizationProperties.getProperty("email_confirm.subject");
        String message = String.format("%s, %s %s. %s %s",
                LocalizationProperties.getProperty("email_confirm.greeting"), user.getFirstName(), user.getSurname(),
                LocalizationProperties.getProperty("email_confirm.message"), confirmLink);
        mailSender.send(user.getEmail(), subject, message);
    }
}
