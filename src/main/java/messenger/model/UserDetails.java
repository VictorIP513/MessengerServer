package messenger.model;

import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "user_photo")
    private String userPhoto;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
