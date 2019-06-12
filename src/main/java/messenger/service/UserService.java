package messenger.service;

import messenger.controller.response.FriendStatus;
import messenger.dao.*;
import messenger.model.*;
import messenger.properties.ServerProperties;
import messenger.properties.LocalizationProperties;
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
    private PasswordStatusDao passwordStatusDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

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
        notificationService.notifyANewFriend(user, friendUser);
    }

    public void deleteFromFriend(User user, User friendUser) {
        friendDao.deleteFromFriend(user, friendUser);
    }

    public void acceptFriendRequest(User user, User friendUser) {
        friendDao.acceptFriendRequest(user, friendUser);
        notificationService.notifyAAcceptFriendRequest(user, friendUser);
    }

    public List<User> getFriends(User user) {
        return friendDao.getFriends(user);
    }

    public List<User> getIncomingRequests(User user) {
        return friendDao.getIncomingRequests(user);
    }

    public List<User> getOutgoingRequests(User user) {
        return friendDao.getOutgoingRequests(user);
    }

    public void changePassword(User user, String newPassword) {
        passwordStatusDao.deleteOldPasswordStatus(user);
        PasswordStatus passwordStatus = new PasswordStatus();
        passwordStatus.setNewPassword(newPassword);
        passwordStatus.setNewPasswordUuid(UUID.randomUUID());
        passwordStatus.setUser(user);
        passwordStatusDao.savePasswordStatusToDatabase(passwordStatus);
        setPasswordChangeMessage(user, passwordStatus);
    }

    public PasswordStatus getPasswordStatus(String passwordCode) {
        if (passwordCode.matches(UUID_REGEXP_VALIDATOR)) {
            UUID passwordUUIDCode = UUID.fromString(passwordCode);
            return passwordStatusDao.getPasswordStatusFromUUIDCode(passwordUUIDCode);
        }
        return null;
    }

    public void confirmChangePassword(PasswordStatus passwordStatus) {
        userDao.changePassword(passwordStatus.getUser(), passwordStatus.getNewPassword());
        passwordStatusDao.deleteChangePasswordStatus(passwordStatus);
    }

    public void cancelChangePassword(PasswordStatus passwordStatus) {
        passwordStatusDao.deleteChangePasswordStatus(passwordStatus);
    }

    public void blockUser(User user, User userToBlock) {
        friendDao.deleteFromFriend(user, userToBlock);
        userDetailsDao.blockUser(user, userToBlock);
    }

    public void unlockUser(User user, User userToUnlock) {
        userDetailsDao.unlockUser(user, userToUnlock);
    }

    public boolean isBlockedUser(User user, User secondUser) {
        return userDetailsDao.isBlockedUser(user, secondUser);
    }

    public boolean getBlockYouStatus(User user, User secondUser) {
        return userDetailsDao.isBlockedUser(secondUser, user);
    }

    public List<User> getBlockedUsersFromUser(User user) {
        return userDetailsDao.getBlockedUsersFromUser(user);
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

    private void setPasswordChangeMessage(User user, PasswordStatus passwordStatus) {
        String serverIp = ServerProperties.getProperty("server.ip");
        String serverPort = ServerProperties.getProperty("server.port");
        String confirmNewPasswordLink = String.format("http://%s:%s/api/confirmChangePasswordStatus/%s",
                serverIp, serverPort, passwordStatus.getNewPasswordUuid());
        String cancelConfirmNewPasswordLink = String.format("http://%s:%s/api/cancelChangePasswordStatus/%s",
                serverIp, serverPort, passwordStatus.getNewPasswordUuid());

        String subject = LocalizationProperties.getProperty("change_password.subject");
        String message = String.format("%s, %s %s. %s %s.%n %s %s",
                LocalizationProperties.getProperty("change_password.greeting"), user.getFirstName(), user.getSurname(),
                LocalizationProperties.getProperty("password_change_confirm.message"), confirmNewPasswordLink,
                LocalizationProperties.getProperty("password_change_cancel.message"), cancelConfirmNewPasswordLink);
        mailSender.send(user.getEmail(), subject, message);
    }
}
