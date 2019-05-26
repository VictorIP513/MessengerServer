package messenger.dao;

import messenger.model.Message;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class MessageDao {

    @Autowired
    private DatabaseUtils databaseUtils;

    public void saveMessage(Message message) {
        databaseUtils.saveObject(message);
    }
}
