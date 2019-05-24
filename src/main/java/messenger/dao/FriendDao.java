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
        setFriendStatus(user, friendUser, FriendStatus.FRIEND_REQUEST_HAS_BEEN_SENT);
        setFriendStatus(friendUser, user, FriendStatus.INCOMING_FRIEND_REQUEST);
    }

    @SuppressWarnings("squid:S2234")
    public void deleteFromFriend(User user, User friendUser) {
        Friend firstFriendStatus = getFriendStatus(user, friendUser);
        Friend secondFriendStatus = getFriendStatus(friendUser, user);

        databaseUtils.deleteObject(firstFriendStatus);
        databaseUtils.deleteObject(secondFriendStatus);
    }

    @SuppressWarnings("squid:S2234")
    public void acceptFriendRequest(User user, User friendUser) {
        Friend firstFriendStatus = getFriendStatus(user, friendUser);
        Friend secondFriendStatus = getFriendStatus(friendUser, user);

        firstFriendStatus.setFriendStatus((short) FriendStatus.USER_IS_FRIEND.getStatusInDatabase());
        secondFriendStatus.setFriendStatus((short) FriendStatus.USER_IS_FRIEND.getStatusInDatabase());

        databaseUtils.updateObject(firstFriendStatus);
        databaseUtils.updateObject(secondFriendStatus);
    }

    public List<User> getFriends(User user) {
        return getUsersFromFriendStatus(user, FriendStatus.USER_IS_FRIEND);
    }

    public List<User> getIncomingRequests(User user) {
        return getUsersFromFriendStatus(user, FriendStatus.INCOMING_FRIEND_REQUEST);
    }

    public List<User> getOutgoingRequests(User user) {
        return getUsersFromFriendStatus(user, FriendStatus.FRIEND_REQUEST_HAS_BEEN_SENT);
    }

    private List<User> getUsersFromFriendStatus(User user, FriendStatus friendStatus) {
        int friendStatusInDatabase = friendStatus.getStatusInDatabase();
        String query = String.format("FROM Friend WHERE user =: user AND friendStatus = %d", friendStatusInDatabase);
        Map<String, Object> params = Collections.singletonMap("user", user);
        List<Friend> friends = databaseUtils.getObjectsFromQuery(Friend.class, query, params);
        return friends.stream()
                .map(Friend::getFriendUser)
                .collect(Collectors.toList());
    }

    private void setFriendStatus(User user, User friendUser, FriendStatus friendStatus) {
        Friend friend = getFriendStatus(user, friendUser);
        if (friend == null) {
            Friend newFriend = new Friend();
            newFriend.setFriendStatus((short) friendStatus.getStatusInDatabase());
            newFriend.setUser(user);
            newFriend.setFriendUser(friendUser);
            databaseUtils.saveObject(newFriend);
        } else {
            friend.setFriendStatus((short) friendStatus.getStatusInDatabase());
            databaseUtils.updateObject(friend);
        }
    }
}
