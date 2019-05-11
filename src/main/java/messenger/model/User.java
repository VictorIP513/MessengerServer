package messenger.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@SuppressWarnings("unused")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue()
    private int id;

    @Type(type = "pg-uuid")
    private UUID uuid;

    @Column(name = "first_name")
    private String firstName;

    private String surname;
    private String login;
    private String password;
    private String email;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
