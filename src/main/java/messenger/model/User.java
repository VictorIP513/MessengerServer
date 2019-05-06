package messenger.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Type(type = "uuid-char")
    private UUID uuid;

    @Column(name = "first_name")
    private String firstName;

    private String surname;
    private String login;
    private String password;
    private String email;

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
