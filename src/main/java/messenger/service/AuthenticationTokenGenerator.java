package messenger.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationTokenGenerator {

    private static final int AUTHENTICATION_TOKEN_LENGTH = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final List<Character> SYMBOLS;

    static {
        SYMBOLS = new ArrayList<>();
        for (char digit = '0'; digit <= '9'; ++digit) {
            SYMBOLS.add(digit);
        }
        for (char lowerCaseCharacter = 'a'; lowerCaseCharacter <= 'z'; ++lowerCaseCharacter) {
            SYMBOLS.add(lowerCaseCharacter);
        }
        for (char upperCaseCharacter = 'A'; upperCaseCharacter <= 'Z'; ++upperCaseCharacter) {
            SYMBOLS.add(upperCaseCharacter);
        }
    }

    private AuthenticationTokenGenerator() {

    }

    public static String getToken() {
        char[] token = new char[AUTHENTICATION_TOKEN_LENGTH];
        for (int i = 0; i < AUTHENTICATION_TOKEN_LENGTH; ++i) {
            token[i] = SYMBOLS.get(SECURE_RANDOM.nextInt(SYMBOLS.size()));
        }
        return new String(token);
    }
}
