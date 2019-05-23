package messenger.dao;

import messenger.controller.response.FriendStatus;
import messenger.model.Friend;
import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Component
public class FriendDao {

    @Autowired
    private DatabaseUtils databaseUtils;

    public Friend getFriendStatus(User user, User friendUser) {
        String query = "FROM Friend WHERE user =: user AND friendUser =: friendUser";
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("friendUser", friendUser);
        return databaseUtils.getUniqueObjectFromQuery(Friend.class, query, params);
    }

    public void addToFriend(User user, User friendUser) {
        Friend friend = getFriendStatus(user, friendUser);
        if (friend == null) {
            Friend newFriend = new Friend();
            newFriend.setFriendStatus((short) FriendStatus.FRIEND_REQUEST_HAS_BEEN_SENT.getStatusInDatabase());
            newFriend.setUser(user);
            newFriend.setFriendUser(friendUser);
            databaseUtils.saveObject(newFriend);
        } else {
            friend.setFriendStatus((short) FriendStatus.FRIEND_REQUEST_HAS_BEEN_SENT.getStatusInDatabase());
            databaseUtils.updateObject(friend);
        }
    }

    public List<User> getFriends(User user) {
        int friendStatusInDatabase = FriendStatus.USER_IS_FRIEND.getStatusInDatabase();
        String query = String.format("FROM Friend WHERE user =: user AND friendStatus = %d", friendStatusInDatabase);
        Map<String, Object> params = Collections.singletonMap("user", user);
        List<Friend> friends = databaseUtils.getObjectsFromQuery(Friend.class, query, params);
        return friends.stream()
                .map(Friend::getFriendUser)
                .collect(Collectors.toList());
    }
}
