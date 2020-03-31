package nl.reservefast.authservice.helper;

import org.passay.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class PasswordHelper {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 20;

    private Random random = new Random();

    private List<CharacterRule> characterRules = Arrays.asList(
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1)
    );

    private List<Rule> rules = Arrays.asList(
            new LengthRule(MIN_LENGTH, MAX_LENGTH),
            new WhitespaceRule(),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1)
    );

    private PasswordGenerator passwordGenerator = new PasswordGenerator();
    private PasswordValidator passwordValidator = new PasswordValidator(rules);

    public String generatePassword() {
        return passwordGenerator.generatePassword(random.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH, characterRules);
    }

    public boolean isValid(String password) {
        return passwordValidator.validate(new PasswordData(password)).isValid();
    }

    public boolean isMatch(String password, String userPassword) {
        return BCrypt.checkpw(password, userPassword);
    }

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
