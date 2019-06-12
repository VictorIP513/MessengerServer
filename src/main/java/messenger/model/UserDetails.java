package messenger.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "user_photo")
    private String userPhoto;

    @Column(name = "last_online")
    private Timestamp lastOnline;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "blocked_users", joinColumns = @JoinColumn(name = "user_details_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id"))
    private Set<User> blockedUsers;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Timestamp getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Timestamp lastOnline) {
        this.lastOnline = lastOnline;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }
}
