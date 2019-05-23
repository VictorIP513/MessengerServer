package messenger.service;

import messenger.controller.response.FriendStatus;
import messenger.dao.*;
import messenger.model.EmailStatus;
import messenger.model.Friend;
import messenger.model.User;
import messenger.model.UserDetails;
import messenger.view.LocalizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private UserDetailsDao userDetailsDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private FileStorageService fileStorageService;

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

    public User getUserByAuthenticationToken(String authenticationToken) {
        return tokenDao.getUserByAuthenticationToken(authenticationToken);
    }

    public User getUserByLogin(String login) {
        return userDao.getUserByLogin(login);
    }

    public void uploadUserPhoto(String pathToPhoto, User user) {
        String pathToOldPhoto = userDetailsDao.uploadUserPhotoAndGetOldPhoto(pathToPhoto, user);
        if (pathToOldPhoto != null) {
            fileStorageService.deleteFile(pathToOldPhoto);
        }
    }

    public UserDetails getUserDetailsByUser(User user) {
        return userDetailsDao.getUserDetailsByUser(user);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public FriendStatus getFriendStatus(User user, User friendUser) {
        Friend friend = friendDao.getFriendStatus(user, friendUser);
        if (friend == null) {
            return FriendStatus.USER_IS_NOT_FRIEND;
        }
        return FriendStatus.getFriendStatusFromStatusInDatabase(friend.getFriendStatus());
    }

    public void addToFriend(User user, User friendUser) {
        friendDao.addToFriend(user, friendUser);
    }

    public List<User> getFriends(User user) {
        return friendDao.getFriends(user);
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
