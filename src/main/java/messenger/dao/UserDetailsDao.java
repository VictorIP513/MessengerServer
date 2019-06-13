package messenger.dao;

import messenger.model.User;
import messenger.model.UserDetails;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Component
public class UserDetailsDao {

    private static final String USER_COLUMN_NAME = "user";

    @Autowired
    private DatabaseUtils databaseUtils;

    public String uploadUserPhotoAndGetOldPhoto(String pathToPhoto, User user) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            userDetails = createUserDetails(user, pathToPhoto, Collections.emptySet(), null);
            databaseUtils.saveObject(userDetails);
            return null;
        } else {
            String pathToOldPhoto = userDetails.getUserPhoto();
            userDetails.setUserPhoto(pathToPhoto);
            databaseUtils.updateObject(userDetails);
            return pathToOldPhoto;
        }
    }

    public void setLastOnlineDate(User user, Timestamp lastOnlineDate) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            userDetails = createUserDetails(user, null, Collections.emptySet(), lastOnlineDate);
            databaseUtils.saveObject(userDetails);
        } else {
            userDetails.setLastOnline(lastOnlineDate);
            databaseUtils.updateObject(userDetails);
        }
    }

    public UserDetails getUserDetailsByUser(User user) {
        return databaseUtils.getUniqueObjectByField(UserDetails.class, USER_COLUMN_NAME, user);
    }

    public void blockUser(User user, User userToBlock) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            userDetails = createUserDetails(user, null, Collections.singleton(userToBlock), null);
            databaseUtils.saveObject(userDetails);
        } else {
            Set<User> blockedUsers = userDetails.getBlockedUsers();
            blockedUsers.add(userToBlock);
            databaseUtils.updateObject(userDetails);
        }
    }

    public void unlockUser(User user, User userToUnlock) {
        UserDetails userDetails = getUserDetailsByUser(user);
        Set<User> blockedUsers = userDetails.getBlockedUsers();
        blockedUsers.remove(userToUnlock);
        databaseUtils.updateObject(userDetails);
    }

    public boolean isBlockedUser(User user, User secondUser) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            return false;
        }
        Set<User> blockedUsers = userDetails.getBlockedUsers();
        return blockedUsers.contains(secondUser);
    }

    public List<User> getBlockedUsersFromUser(User user) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(userDetails.getBlockedUsers());
    }

    public Timestamp getLastOnlineDate(User user) {
        UserDetails userDetails = getUserDetailsByUser(user);
        if (userDetails == null) {
            return null;
        }
        return userDetails.getLastOnline();
    }

    private UserDetails createUserDetails(User user, String pathToPhoto, Set<User> blockedUsers, Timestamp lastOnline) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUser(user);
        userDetails.setUserPhoto(pathToPhoto);
        userDetails.setBlockedUsers(blockedUsers);
        userDetails.setLastOnline(lastOnline);
        return userDetails;
    }
}
