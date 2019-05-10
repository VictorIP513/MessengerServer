package messenger.model;

import javax.persistence.*;

@Entity
@Table(name = "email_status")
public class EmailStatus {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "confirm_status")
    private boolean confirmStatus;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void setConfirmStatus(boolean confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
}
