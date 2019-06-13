package messenger.model;

import javax.persistence.*;
import java.sql.Timestamp;

@SuppressWarnings("unused")
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    @Column(name = "is_photo")
    private boolean isPhoto;

    private String text;
    private Timestamp date;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setMessageIsPhoto(boolean value) {
        isPhoto = value;
    }
}
