package messenger.model;

import javax.persistence.*;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
@Table(name = "dialogs")
public class Dialog {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "dialog_photo")
    private String dialogPhoto;

    @Column(name = "dialog_name")
    private String dialogName;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "last_message_id"),
            value = @JoinColumn(name = "last_message_id", referencedColumnName = "id"))
    private Message lastMessage;

    @ManyToMany
    @JoinTable(name = "users_from_dialogs", joinColumns = @JoinColumn(name = "dialog_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    public String getDialogPhoto() {
        return dialogPhoto;
    }

    public void setDialogPhoto(String dialogPhoto) {
        this.dialogPhoto = dialogPhoto;
    }

    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
