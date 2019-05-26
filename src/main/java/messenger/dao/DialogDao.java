package messenger.dao;

import messenger.model.Dialog;
import messenger.model.Message;
import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Component
public class DialogDao {

    @Autowired
    private DatabaseUtils databaseUtils;

    public Dialog createDialog(Dialog dialog) {
        return databaseUtils.saveObjectAndGetId(dialog);
    }

    public Dialog getDialog(int id) {
        return databaseUtils.getObjectById(Dialog.class, id);
    }

    public Dialog getDialog(User user, User secondUser) {
        Set<User> users = new HashSet<>();
        users.add(user);
        users.add(secondUser);
        List<Dialog> dialogs = databaseUtils.getAllRecordsFromTable(Dialog.class);
        for (Dialog dialog : dialogs) {
            if (dialog.getUsers().equals(users)) {
                return dialog;
            }
        }
        return null;
    }

    public void updateDialog(Dialog dialog) {
        databaseUtils.updateObject(dialog);
    }

    public void addMessageToDialog(Dialog dialog, Message message) {
        Set<Message> messages = dialog.getMessages();
        messages.add(message);
        databaseUtils.updateObject(dialog);
    }

    public List<Dialog> getAllDialogs(User user) {
        List<Dialog> dialogs = databaseUtils.getAllRecordsFromTable(Dialog.class);
        dialogs.removeIf(dialog -> !dialog.getUsers().contains(user));
        return dialogs;
    }
}
