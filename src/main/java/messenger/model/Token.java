package messenger.model;

import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "authentication_token")
    private String authenticationToken;

    @OneToOne
    @JoinColumns(foreignKey = @ForeignKey(name = "user_id"), value = @JoinColumn(referencedColumnName = "id"))
    private User user;

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
