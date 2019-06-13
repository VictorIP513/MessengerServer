package messenger.service;

import messenger.dao.DialogDao;
import messenger.dao.MessageDao;
import messenger.model.Dialog;
import messenger.model.Message;
import messenger.model.User;
import messenger.properties.ServerProperties;
import messenger.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Service
public class DialogsService {

    @Autowired
    private DialogDao dialogDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FileStorageService fileStorageService;

    public Dialog createDialog(User user, User userToSend, String lastMessageText) {
        Message lastMessage = createMessage(user, lastMessageText, DateUtils.getCurrentTime(), false);
        messageDao.saveMessage(lastMessage);

        Set<User> usersFromDialog = new HashSet<>();
        usersFromDialog.add(user);
        usersFromDialog.add(userToSend);

        Dialog dialog = new Dialog();
        dialog.setLastMessage(lastMessage);
        dialog.setUsers(usersFromDialog);
        dialog.setMessages(Collections.singleton(lastMessage));

        dialog = dialogDao.createDialog(dialog);
        return dialog;
    }

    public Dialog getDialog(int id) {
        return dialogDao.getDialog(id);
    }

    public Dialog getDialog(User user, User secondUser) {
        return dialogDao.getDialog(user, secondUser);
    }

    public Message sendMessage(String messageText, Dialog dialog, User user) {
        Message message = createMessage(user, messageText, DateUtils.getCurrentTime(), false);
        dialogDao.addMessageToDialog(dialog, message);

        Set<User> usersToSendNotification = new HashSet<>(dialog.getUsers());
        usersToSendNotification.remove(user);
        notificationService.notifyANewMessage(usersToSendNotification, message);
        return message;
    }

    public List<Dialog> getAllDialogs(User user) {
        List<Dialog> dialogs = dialogDao.getAllDialogs(user);
        for (Dialog dialog : dialogs) {
            dialog.setMessages(null);
        }
        return dialogs;
    }

    public Message sendImage(String pathToFile, Dialog dialog, User user) {
        String imageUrl = createUrlToImage(pathToFile);
        Message message = createMessage(user, imageUrl, DateUtils.getCurrentTime(), true);
        dialogDao.addMessageToDialog(dialog, message);

        Set<User> usersToSendNotification = new HashSet<>(dialog.getUsers());
        usersToSendNotification.remove(user);
        notificationService.notifyANewMessage(usersToSendNotification, message);
        return message;
    }

    private Message createMessage(User user, String text, Timestamp date, boolean isPhoto) {
        Message message = new Message();
        message.setUser(user);
        message.setText(text);
        message.setDate(date);
        message.setMessageIsPhoto(isPhoto);
        return message;
    }

    private String createUrlToImage(String pathToImage) {
        String serverIp = ServerProperties.getProperty("server.ip");
        String serverPort = ServerProperties.getProperty("server.port");
        return String.format("http://%s:%s/api/getImage/%s", serverIp, serverPort, pathToImage);
    }
}
