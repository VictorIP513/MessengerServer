package messenger.model;

import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "friend_user_id"),
            value = @JoinColumn(name = "friend_user_id", referencedColumnName = "id"))
    private User friendUser;

    @Column(name = "friend_status")
    private short friendStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }

    public short getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(short friendStatus) {
        this.friendStatus = friendStatus;
    }
}
