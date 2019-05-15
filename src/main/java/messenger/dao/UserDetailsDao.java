package messenger.dao;

import messenger.model.User;
import messenger.model.UserDetails;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class UserDetailsDao {

    private static final String USER_COLUMN_NAME = "user";

    @Autowired
    private DatabaseUtils databaseUtils;


    public String uploadUserPhotoAndGetOldPhoto(String pathToPhoto, User user) {
        UserDetails userDetails = databaseUtils.getUniqueObjectByField(UserDetails.class, USER_COLUMN_NAME, user);
        if (userDetails == null) {
            UserDetails newUserDetails = new UserDetails();
            newUserDetails.setUserPhoto(pathToPhoto);
            newUserDetails.setUser(user);
            databaseUtils.saveObject(newUserDetails);
            return null;
        } else {
            String pathToOldPhoto = userDetails.getUserPhoto();
            userDetails.setUserPhoto(pathToPhoto);
            databaseUtils.updateObject(userDetails);
            return pathToOldPhoto;
        }
    }
}
