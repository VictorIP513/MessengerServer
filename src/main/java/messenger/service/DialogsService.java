package messenger.service;

import messenger.dao.DialogDao;
import messenger.dao.MessageDao;
import messenger.model.Dialog;
import messenger.model.Message;
import messenger.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("unused")
@Service
public class DialogsService {

    @Autowired
    private DialogDao dialogDao;

    @Autowired
    private MessageDao messageDao;

    public Dialog createDialog(User user, User userToSend, String lastMessageText) {
        Message lastMessage = new Message();
        lastMessage.setDate(new Timestamp(new Date().getTime()));
        lastMessage.setText(lastMessageText);
        lastMessage.setUser(user);
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
        Message message = new Message();
        message.setUser(user);
        message.setText(messageText);
        message.setDate(new Timestamp(new Date().getTime()));

        dialogDao.addMessageToDialog(dialog, message);
        return message;
    }

    public List<Dialog> getAllDialogs(User user) {
        List<Dialog> dialogs = dialogDao.getAllDialogs(user);
        for (Dialog dialog : dialogs) {
            dialog.setMessages(null);
        }
        return dialogs;
    }
}
