package messenger.controller.response;

import java.util.Arrays;

public enum FriendStatus {

    USER_IS_NOT_FRIEND(0),
    USER_IS_FRIEND(1),
    FRIEND_REQUEST_HAS_BEEN_SENT(2),
    INCOMING_FRIEND_REQUEST(3);

    private int statusInDatabase;

    FriendStatus(int statusInDatabase) {
        this.statusInDatabase = statusInDatabase;
    }

    public int getStatusInDatabase() {
        return statusInDatabase;
    }

    public static FriendStatus getFriendStatusFromStatusInDatabase(int statusInDatabase) {
        return Arrays.stream(FriendStatus.values())
                .filter(friendStatus -> friendStatus.statusInDatabase == statusInDatabase)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Incorrect status value"));
    }
}
