package messenger.dao;

import messenger.model.Token;
import messenger.model.User;
import messenger.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class TokenDao {

    private static final String USER_COLUMN_NAME = "user";

    @Autowired
    private DatabaseUtils databaseUtils;

    public void setNewAuthenticationTokenToUser(String authenticationToken, User user) {
        Token token = databaseUtils.getUniqueObjectByField(Token.class, USER_COLUMN_NAME, user);
        if (token == null) {
            Token newToken = new Token();
            newToken.setAuthenticationToken(authenticationToken);
            newToken.setUser(user);
            databaseUtils.saveObject(newToken);
        } else {
            token.setAuthenticationToken(authenticationToken);
            databaseUtils.updateObject(token);
        }
    }
}
