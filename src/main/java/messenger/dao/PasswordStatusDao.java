package messenger.dao;

import messenger.model.PasswordStatus;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SuppressWarnings("unused")
@Component
public class PasswordStatusDao {

    @Autowired
    private DatabaseUtils databaseUtils;

    public void savePasswordStatusToDatabase(PasswordStatus passwordStatus) {
        databaseUtils.saveObject(passwordStatus);
    }

    public PasswordStatus getPasswordStatusFromUUIDCode(UUID passwordUUIDCode) {
        return databaseUtils.getUniqueObjectByField(PasswordStatus.class,
                "newPasswordUuid", passwordUUIDCode);
    }

    public void deleteChangePasswordStatus(PasswordStatus passwordStatus) {
        databaseUtils.deleteObject(passwordStatus);
    }
}
