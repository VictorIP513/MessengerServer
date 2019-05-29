package messenger.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
@Table(name = "password_status")
public class PasswordStatus {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    @Type(type = "pg-uuid")
    @Column(name = "new_password_uuid")
    private UUID newPasswordUuid;

    @Column(name = "new_password")
    private String newPassword;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getNewPasswordUuid() {
        return newPasswordUuid;
    }

    public void setNewPasswordUuid(UUID newPasswordUuid) {
        this.newPasswordUuid = newPasswordUuid;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
